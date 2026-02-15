package ExitModule;

import EntryModule.Ticket;
import FineModule.FineManager;
import FineModule.FineScheme;
import coreParkingSystem.FineDAO;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.ParkingSpotDAO;
import coreParkingSystem.ReceiptDAO;
import coreParkingSystem.VehicleDAO;
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
    
    private static Map<String, ExitRecord> exitRecords = new HashMap<>();
    private static Map<String, PendingExit> pendingExits = new HashMap<>();
    
    private LocalDateTime testTime = null;
    
    public ExitSystem() {
        this.feeCalculator = new FeeCalculator();
        this.fineManager = new FineManager();
        this.fineDAO = new FineDAO();
        this.receiptDAO = new ReceiptDAO();
        this.vehicleDAO = new VehicleDAO();
    }
    
    public void setTestTime(LocalDateTime time) {
        this.testTime = time;
    }
    
    public void clearTestTime() {
        this.testTime = null;
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
        
        String spotId;
        String spotTypeString;
        
        if (spot != null) {
            spotId = parseSpotId(spot.getSpotID());
            spotTypeString = spot.getSpotType().name();
        } else {
            spotId = "Not Parked";
            spotTypeString = "REGULAR"; 
        }
        
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime initiationTime = getCurrentTime();
        
        double parkingFee = feeCalculator.calculateParkingFee(
            entryTime, initiationTime, spotTypeString, ticket.getVehicleType()
        );
        
        double currentFines = calculateFines(ticket, initiationTime);
        double unpaidFines = vehicleDAO.getAccumulatedFines(licensePlate);
        double totalFines = currentFines + unpaidFines;
        double totalDue = parkingFee + totalFines;
        
        LocalDateTime nextHourThreshold = calculateNextHourThreshold(entryTime, initiationTime);
        LocalDateTime twentyFourHourThreshold = entryTime.plusHours(24);
        
        PendingExit pending = new PendingExit(
            licensePlate, ticket, spotId, entryTime, initiationTime, initiationTime,
            parkingFee, currentFines, unpaidFines, totalFines, totalDue,
            nextHourThreshold, twentyFourHourThreshold
        );
        
        pendingExits.put(licensePlate, pending);
        return pending;
    }
    
    public boolean checkForUpdates(String licensePlate) {
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) return false;
        
        LocalDateTime confirmationTime = getCurrentTime();
        LocalDateTime initiationTime = pending.getInitiatedTime();
        
        if (Duration.between(initiationTime, confirmationTime).toSeconds() < 1) return false;
        
        vehicleDAO.syncTotalFines(licensePlate);

        Ticket ticket = pending.getTicket();
        ParkingSpotDAO spotDAO = new ParkingSpotDAO();
        ParkingSpot spot = spotDAO.findByPlate(licensePlate);
        
        String spotTypeString = (spot != null) ? spot.getSpotType().name() : "REGULAR";

        double currentParkingFee = feeCalculator.calculateParkingFee(
            pending.getEntryTime(), confirmationTime, 
            spotTypeString, ticket.getVehicleType()
        );
        
        double currentFines = calculateFines(ticket, confirmationTime);
        double unpaidFines = vehicleDAO.getAccumulatedFines(licensePlate);
        double currentTotalDue = currentParkingFee + currentFines + unpaidFines;
        
        boolean hasChanged = false;

        if (Math.abs(currentParkingFee - pending.getParkingFee()) > 0.01 || currentFines > pending.getCurrentFines()) {
            hasChanged = true;
            
            pending.setParkingFee(currentParkingFee);
            pending.setCurrentFines(currentFines);
            pending.setTotalFines(currentFines + unpaidFines);
            pending.setTotalDue(currentTotalDue);
            pending.setLastCheckedTime(confirmationTime);
            pending.setNextHourThreshold(calculateNextHourThreshold(pending.getEntryTime(), confirmationTime));
        }
        
        return hasChanged;
    }
    
    public Receipt confirmExit(String licensePlate, double parkingFeePayment, 
                               double finePayment, String paymentMethod) {
        
        ParkingSpotDAO spotDAO = new ParkingSpotDAO();
        ParkingSpot spot = spotDAO.findByPlate(licensePlate);
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) {
            return null;
        }
        
        if (checkForUpdates(licensePlate)) {
            return null; 
        }
        
        LocalDateTime confirmationTime = getCurrentTime();
        Ticket ticket = pending.getTicket();
        String spotId = pending.getSpotId();
        
        double parkingFee = pending.getParkingFee();
        double totalFinesAvailable = pending.getTotalFines();
        
        if (totalFinesAvailable >= 500.0 && finePayment < totalFinesAvailable) {
            System.out.println("BLOCK: Fines exceed RM500. Full payment required.");
            return null;
        }

        double parkingFeePaidAmount = 0.0;
        double change = 0.0;
        
        if (paymentMethod.equalsIgnoreCase("CARD")) {
            if (Math.abs(parkingFeePayment - parkingFee) < 0.01) {
                parkingFeePaidAmount = parkingFee;
            } else {
                return null;
            }
        } else if (paymentMethod.equalsIgnoreCase("CASH")) {
            if (parkingFeePayment >= parkingFee) {
                parkingFeePaidAmount = parkingFee;
                change = parkingFeePayment - parkingFee;
            } else {
                return null;
            }
        } else {
            return null;
        }
        
        double finesPaidNow = 0.0;
        
        if (finePayment > 0) {
            if (Math.abs(finePayment - totalFinesAvailable) < 0.01) {
                finesPaidNow = totalFinesAvailable;
                fineDAO.markFinesAsPaid(licensePlate);
            } else {
                return null;
            }
        } else {
            double currentNewFine = pending.getCurrentFines();
            if (currentNewFine > 0) {
                 fineDAO.createFine(licensePlate, currentNewFine, "Overstay/Violation");
            }
        }
        
        vehicleDAO.syncTotalFines(licensePlate);

        String receiptSpotType = "NONE";
        if (spot != null && !spotId.equals("Not Parked")) {
            ParkingLot.getInstance().setSpotStatus(spotId, ParkingSpot.Status.AVAILABLE);
            
            ParkingSpot systemSpot = ParkingLot.getInstance().getSpotById(spotId);
            if (systemSpot != null) {
                systemSpot.setCurrentlyParkedVehicleID(null); 
                ParkingLot.getInstance().updateSpotOccupancy(systemSpot); 
            }
            
            receiptSpotType = spot.getSpotType().name();
        } else {
            receiptSpotType = "REGULAR"; 
        }

        ParkingLot.getInstance().closeTicket(ticket.getTicketID(), licensePlate);
        pendingExits.remove(licensePlate);
        
        double totalPaid = parkingFeePaidAmount + finesPaidNow;
        double remainingFines = vehicleDAO.getAccumulatedFines(licensePlate); 

        Receipt receipt = new Receipt(
            licensePlate,
            ticket.getEntryTime(),
            confirmationTime,
            spotId,
            receiptSpotType,
            ticket.getVehicleType(),
            calculateHoursParked(ticket.getEntryTime(), confirmationTime),
            parkingFee,
            finesPaidNow,
            remainingFines,
            parkingFeePaidAmount,
            finesPaidNow,
            totalPaid,
            change,
            paymentMethod,
            ticket.getTicketID(),
            true
        );
        
        receiptDAO.create(receipt);
        return receipt;
    }

    private LocalDateTime calculateNextHourThreshold(LocalDateTime entryTime, LocalDateTime currentTime) {
        Duration duration = Duration.between(entryTime, currentTime);
        long hoursParked = (long) Math.ceil(duration.toMinutes() / 60.0);
        return entryTime.plusMinutes(hoursParked * 60);
    }

    private String parseSpotId(String spotId) {
        return spotId;
    }

    private double calculateFines(Ticket ticket, LocalDateTime exitTime) {
        double hoursParked = calculateHoursParked(ticket.getEntryTime(), exitTime);
        if (hoursParked <= 24) return 0.0;
        return fineManager.calculateFine((int) hoursParked);
    }

    private double calculateHoursParked(LocalDateTime entryTime, LocalDateTime exitTime) {
        Duration duration = Duration.between(entryTime, exitTime);
        double totalMinutes = duration.toMinutes();
        if (totalMinutes <= 0) return 1.0;
        return Math.ceil(totalMinutes / 60.0);
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
    
    public static class ExitRecord {
    }
}