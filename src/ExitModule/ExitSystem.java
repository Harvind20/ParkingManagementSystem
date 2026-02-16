package ExitModule;

import EntryModule.Ticket;
import FineModule.FineManager;
import FineModule.FineScheme;
import coreParkingSystem.DatabaseConnection;
import coreParkingSystem.FineDAO;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.ParkingSpotDAO;
import coreParkingSystem.ReceiptDAO;
import coreParkingSystem.VehicleDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExitSystem {
    private FeeCalculator feeCalculator;
    private FineManager fineManager;
    private FineDAO fineDAO;
    private ReceiptDAO receiptDAO;
    private VehicleDAO vehicleDAO;
    
    private static Map<String, PendingExit> pendingExits = new HashMap<>();
    
    private LocalDateTime testTime = null;
    
    public ExitSystem() {
        this.feeCalculator = new FeeCalculator();
        this.fineManager = new FineManager();
        this.fineDAO = new FineDAO();
        this.receiptDAO = new ReceiptDAO();
        this.vehicleDAO = new VehicleDAO();
    }
    
    private LocalDateTime getCurrentTime() {
        if (testTime != null) return testTime;
        return LocalDateTime.now();
    }
    
    public PendingExit initiateExit(String licensePlate) {
        vehicleDAO.syncTotalFines(licensePlate);

        Ticket ticket = ParkingLot.getInstance().getTicketByPlate(licensePlate);
        ParkingSpotDAO spotDAO = new ParkingSpotDAO();
        ParkingSpot spot = spotDAO.findByPlate(licensePlate);
        
        if (ticket == null) {
            return null;
        }
        
        String spotId = (spot != null) ? spot.getSpotID() : "Not Parked";
        String spotTypeString = (spot != null) ? spot.getSpotType().name() : "REGULAR";
        
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime initiationTime = getCurrentTime();

        // Use DAO to get real vehicle type
        String realVehicleType = vehicleDAO.getVehicleType(licensePlate);

        double parkingFee = feeCalculator.calculateParkingFee(
            entryTime, initiationTime, spotTypeString, realVehicleType
        );

        double calculatedSessionFine = calculateAllFines(ticket, spot, initiationTime, licensePlate, realVehicleType);

        double totalDbFines = vehicleDAO.getAccumulatedFines(licensePlate);
        double recordedSessionFine = getExistingSessionFineAmount(licensePlate);

        double historicalFines = Math.max(0, totalDbFines - recordedSessionFine);

        double totalFinesToPay = historicalFines + calculatedSessionFine;
        double totalDue = parkingFee + totalFinesToPay;
        
        LocalDateTime nextHourThreshold = calculateNextHourThreshold(entryTime, initiationTime);
        LocalDateTime twentyFourHourThreshold = entryTime.plusHours(24);
        
        PendingExit pending = new PendingExit(
            licensePlate, ticket, spotId, entryTime, initiationTime, initiationTime,
            parkingFee, calculatedSessionFine, historicalFines, totalFinesToPay, totalDue,
            nextHourThreshold, twentyFourHourThreshold
        );
        
        pendingExits.put(licensePlate, pending);
        return pending;
    }
    
    public boolean checkForUpdates(String licensePlate) {
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) return false;
        
        LocalDateTime confirmationTime = getCurrentTime();
        if (Duration.between(pending.getInitiatedTime(), confirmationTime).toSeconds() < 1) return false;

        vehicleDAO.syncTotalFines(licensePlate);

        Ticket ticket = pending.getTicket();
        ParkingSpotDAO spotDAO = new ParkingSpotDAO();
        ParkingSpot spot = spotDAO.findByPlate(licensePlate);
        String spotTypeString = (spot != null) ? spot.getSpotType().name() : "REGULAR";

        String realVehicleType = vehicleDAO.getVehicleType(licensePlate);

        double currentParkingFee = feeCalculator.calculateParkingFee(
            pending.getEntryTime(), confirmationTime, spotTypeString, realVehicleType
        );

        double calculatedSessionFine = calculateAllFines(ticket, spot, confirmationTime, licensePlate, realVehicleType);
        
        double totalDbFines = vehicleDAO.getAccumulatedFines(licensePlate);
        double recordedSessionFine = getExistingSessionFineAmount(licensePlate);
        double historicalFines = Math.max(0, totalDbFines - recordedSessionFine);
        
        double totalFinesToPay = historicalFines + calculatedSessionFine;
        double currentTotalDue = currentParkingFee + totalFinesToPay;
        
        boolean hasChanged = false;

        if (Math.abs(currentParkingFee - pending.getParkingFee()) > 0.01 || 
            calculatedSessionFine > pending.getCurrentFines()) {
            hasChanged = true;
            
            pending.setParkingFee(currentParkingFee);
            pending.setCurrentFines(calculatedSessionFine);
            pending.setTotalFines(totalFinesToPay);
            pending.setTotalDue(currentTotalDue);
            pending.setLastCheckedTime(confirmationTime);
            pending.setNextHourThreshold(calculateNextHourThreshold(pending.getEntryTime(), confirmationTime));
        }
        
        return hasChanged;
    }
    
    public Receipt confirmExit(String licensePlate, double parkingFeePayment, 
                               double finePayment, String paymentMethod) {
        
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) return null;
        if (checkForUpdates(licensePlate)) return null; 
        
        double totalFinesAvailable = pending.getTotalFines();

        if (totalFinesAvailable >= 500.0 && finePayment < totalFinesAvailable) {
            System.out.println("BLOCK: Total fines (" + totalFinesAvailable + ") exceed RM500. Full payment required.");
            return null;
        }

        double parkingFee = pending.getParkingFee();
        double parkingFeePaidAmount = 0.0;
        double change = 0.0;

        if (paymentMethod.equalsIgnoreCase("CARD") && Math.abs(parkingFeePayment - parkingFee) < 0.01) {
            parkingFeePaidAmount = parkingFee;
        } else if (paymentMethod.equalsIgnoreCase("CASH") && parkingFeePayment >= parkingFee) {
            parkingFeePaidAmount = parkingFee;
            change = parkingFeePayment - parkingFee;
        } else {
            return null;
        }

        double finesPaidNow = 0.0;
        double currentSessionFine = pending.getCurrentFines();

        updateFineRecordToFinalAmount(licensePlate, currentSessionFine);

        if (finePayment > 0) {
            if (Math.abs(finePayment - totalFinesAvailable) < 0.01) {
                finesPaidNow = totalFinesAvailable;
                fineDAO.markFinesAsPaid(licensePlate);
            } else {
                return null;
            }
        }

        vehicleDAO.syncTotalFines(licensePlate);

        ParkingSpotDAO spotDAO = new ParkingSpotDAO();
        ParkingSpot spot = spotDAO.findByPlate(licensePlate);
        String spotId = pending.getSpotId();
        String receiptSpotType = "REGULAR";

        if (spot != null && !spotId.equals("Not Parked")) {
            ParkingLot.getInstance().setSpotStatus(spotId, ParkingSpot.Status.AVAILABLE);
            ParkingSpot systemSpot = ParkingLot.getInstance().getSpotById(spotId);
            if (systemSpot != null) {
                systemSpot.setCurrentlyParkedVehicleID(null); 
                ParkingLot.getInstance().updateSpotOccupancy(systemSpot); 
            }
            receiptSpotType = spot.getSpotType().name();
        }

        Ticket ticket = pending.getTicket();
        ParkingLot.getInstance().closeTicket(ticket.getTicketID(), licensePlate);
        pendingExits.remove(licensePlate);
        
        double totalPaid = parkingFeePaidAmount + finesPaidNow;
        double remainingFines = vehicleDAO.getAccumulatedFines(licensePlate); 

        String realVehicleType = vehicleDAO.getVehicleType(licensePlate);

        Receipt receipt = new Receipt(
            licensePlate, ticket.getEntryTime(), getCurrentTime(),
            spotId, receiptSpotType, realVehicleType,
            calculateHoursParked(ticket.getEntryTime(), getCurrentTime()),
            parkingFee, finesPaidNow, remainingFines,
            parkingFeePaidAmount, finesPaidNow, totalPaid, change,
            paymentMethod, ticket.getTicketID(), true
        );
        
        receiptDAO.create(receipt);
        return receipt;
    }

    private double getExistingSessionFineAmount(String plate) {
        String sql = "SELECT amount FROM fines WHERE plate_num = ? AND status = 'UNPAID' ORDER BY fine_id DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("amount");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0.0;
    }

    private void updateFineRecordToFinalAmount(String plate, double amount) {
        if (amount <= 0) return;

        if (getExistingSessionFineAmount(plate) > 0) {
            String sql = "UPDATE fines SET amount = ? WHERE plate_num = ? AND status = 'UNPAID' AND fine_id = (SELECT fine_id FROM fines WHERE plate_num=? AND status='UNPAID' ORDER BY fine_id DESC LIMIT 1)";
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, amount);
                pstmt.setString(2, plate);
                pstmt.setString(3, plate);
                pstmt.executeUpdate();
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            fineDAO.createFine(plate, amount, "Exit Fine (Overstay/Violation)");
        }
    }

    private double calculateAllFines(Ticket ticket, ParkingSpot spot, LocalDateTime exitTime, String plate, String realVehicleType) {
        String schemeName = ticket.getFineSchemeAtEntry();
        FineScheme scheme = FineManager.getSchemeByName(schemeName);

        double hoursParked = calculateHoursParked(ticket.getEntryTime(), exitTime);
        int hoursInt = (int) hoursParked;
        
        double totalFine = 0.0;
        
        // 1. Overstay Fine (Applies to everyone > 24 hours)
        totalFine += fineManager.calculateFine(scheme, hoursInt, false);

        // 2. Violation Fines
        boolean isViolation = false;
        
        if (spot != null) {
            boolean isVip = getVipStatus(plate);
            boolean isHandicapped = realVehicleType.equalsIgnoreCase("Handicapped") || 
                                    realVehicleType.equalsIgnoreCase("HandicappedVehicle");
            
            // Reserved Spot Logic: Violation if NOT VIP AND NOT Handicapped
            if (spot.getSpotType() == ParkingSpot.Type.RESERVED) {
                if (!isVip && !isHandicapped) {
                    isViolation = true;
                }
            }
            
            // Handicapped Spot Logic: Violation if NOT Handicapped
            if (spot.getSpotType() == ParkingSpot.Type.HANDICAPPED) {
                if (!isHandicapped) {
                    isViolation = true;
                }
            }
        }

        if (isViolation) {
            totalFine += fineManager.calculateFine(scheme, hoursInt, true);
        }
        return totalFine;
    }
    
    private boolean getVipStatus(String plate) {
        String sql = "SELECT is_vip FROM vehicles WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getBoolean("is_vip");
        } catch (Exception e) {}
        return false;
    }

    private double calculateHoursParked(LocalDateTime entryTime, LocalDateTime exitTime) {
        Duration duration = Duration.between(entryTime, exitTime);
        double totalMinutes = duration.toMinutes();
        if (totalMinutes <= 0) return 1.0;
        return Math.ceil(totalMinutes / 60.0);
    }

    private LocalDateTime calculateNextHourThreshold(LocalDateTime entryTime, LocalDateTime currentTime) {
        Duration duration = Duration.between(entryTime, currentTime);
        long hoursParked = (long) Math.ceil(duration.toMinutes() / 60.0);
        return entryTime.plusMinutes(hoursParked * 60);
    }

    public void setFineScheme(FineScheme scheme) {
        fineManager.setFineScheme(scheme);
    }
    
    public static class PendingExit {
        private String licensePlate;
        private Ticket ticket;
        private String spotId;
        private LocalDateTime entryTime;
        private LocalDateTime initiatedTime;
        private LocalDateTime lastCheckedTime;
        private double parkingFee;
        private double currentFines;
        private double unpaidFines; 
        private double totalFines;  
        private double totalDue;
        private LocalDateTime nextHourThreshold;
        private LocalDateTime twentyFourHourThreshold;
        
        public PendingExit(String licensePlate, Ticket ticket, String spotId,
                           LocalDateTime entryTime, LocalDateTime initiatedTime,
                           LocalDateTime lastCheckedTime, double parkingFee, 
                           double currentFines, double unpaidFines, double totalFines,
                           double totalDue, LocalDateTime nextHourThreshold, 
                           LocalDateTime twentyFourHourThreshold) {
            this.licensePlate = licensePlate;
            this.ticket = ticket;
            this.spotId = spotId;
            this.entryTime = entryTime;
            this.initiatedTime = initiatedTime;
            this.lastCheckedTime = lastCheckedTime;
            this.parkingFee = parkingFee;
            this.currentFines = currentFines;
            this.unpaidFines = unpaidFines;
            this.totalFines = totalFines;
            this.totalDue = totalDue;
            this.nextHourThreshold = nextHourThreshold;
            this.twentyFourHourThreshold = twentyFourHourThreshold;
        }

        public String getLicensePlate() { return licensePlate; }
        public Ticket getTicket() { return ticket; }
        public String getSpotId() { return spotId; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public LocalDateTime getInitiatedTime() { return initiatedTime; }
        public double getParkingFee() { return parkingFee; }
        public double getCurrentFines() { return currentFines; }
        public double getUnpaidFines() { return unpaidFines; }
        public double getTotalFines() { return totalFines; }
        public double getTotalDue() { return totalDue; }
        
        public void setParkingFee(double parkingFee) { this.parkingFee = parkingFee; }
        public void setCurrentFines(double currentFines) { this.currentFines = currentFines; }
        public void setTotalFines(double totalFines) { this.totalFines = totalFines; }
        public void setTotalDue(double totalDue) { this.totalDue = totalDue; }
        public void setLastCheckedTime(LocalDateTime time) { this.lastCheckedTime = time; }
        public void setNextHourThreshold(LocalDateTime threshold) { this.nextHourThreshold = threshold; }
    }
}