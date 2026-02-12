package ExitModule;

import EntryModule.Ticket;
import FineModule.FineManager;
import FineModule.FineScheme;
import coreParkingSystem.FineDAO;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.ReceiptDAO;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExitSystem {
    private FeeCalculator feeCalculator;
    private FineManager fineManager;
    private FineDAO fineDAO; // Database for fines
    private ReceiptDAO receiptDAO; // Database for receipts
    
    // In-memory session tracking (volatile data)
    private Map<String, ExitRecord> exitRecords;
    private Map<String, PendingExit> pendingExits;
    
    // For testing purposes
    private LocalDateTime testTime = null;
    
    public ExitSystem() {
        this.feeCalculator = new FeeCalculator();
        this.fineManager = new FineManager();
        this.fineDAO = new FineDAO(); // Connect to DB
        this.receiptDAO = new ReceiptDAO(); // Connect to DB
        
        this.exitRecords = new HashMap<>();
        this.pendingExits = new HashMap<>();
    }
    
    public void setTestTime(LocalDateTime time) {
        this.testTime = time;
        System.out.println("[TEST] Time set to: " + time);
    }
    
    public void clearTestTime() {
        this.testTime = null;
        System.out.println("[TEST] Returned to real system time");
    }
    
    private LocalDateTime getCurrentTime() {
        if (testTime != null) return testTime;
        return LocalDateTime.now();
    }
    
    // --- STEP 1: INITIATE EXIT ---
    public PendingExit initiateExit(String licensePlate) {
        System.out.println("\n=== Initiating Exit for Vehicle: " + licensePlate + " ===\n");
        
        // 1. Get Ticket from DB via ParkingLot
        Ticket ticket = ParkingLot.getInstance().getTicketByPlate(licensePlate);
        
        if (ticket == null) {
            System.out.println("ERROR: No active ticket found for license plate: " + licensePlate);
            return null;
        }
        
        String spotId = parseSpotId(ticket.getSpotId());
        
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime initiationTime = getCurrentTime();
        
        // 2. Calculate Parking Fee
        double parkingFee = feeCalculator.calculateParkingFee(
            entryTime, initiationTime, ticket.getSpotType(), ticket.getVehicleType()
        );
        
        // 3. Calculate Fines
        double currentFines = calculateFines(ticket, initiationTime);
        double unpaidFines = getUnpaidFines(licensePlate); // Fetch from DB
        double totalFines = currentFines + unpaidFines;
        double totalDue = parkingFee + totalFines;
        
        LocalDateTime nextHourThreshold = calculateNextHourThreshold(entryTime, initiationTime);
        LocalDateTime twentyFourHourThreshold = entryTime.plusHours(24);
        
        System.out.println("Exit initiated successfully");
        System.out.println("   Initiation Time: " + initiationTime);
        System.out.println("   Parking Fee: RM " + String.format("%.2f", parkingFee));
        System.out.println("   Total Due: RM " + String.format("%.2f", totalDue));
        
        PendingExit pending = new PendingExit(
            licensePlate, ticket, spotId, entryTime, initiationTime, initiationTime,
            parkingFee, currentFines, unpaidFines, totalFines, totalDue,
            nextHourThreshold, twentyFourHourThreshold
        );
        
        pendingExits.put(licensePlate, pending);
        return pending;
    }
    
    // --- STEP 2: CHECK UPDATES ---
    public boolean checkForUpdates(String licensePlate) {
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) return false;
        
        LocalDateTime confirmationTime = getCurrentTime();
        LocalDateTime initiationTime = pending.getInitiatedTime();
        
        if (Duration.between(initiationTime, confirmationTime).toSeconds() < 1) return false;
        
        Ticket ticket = pending.getTicket();
        
        double currentParkingFee = feeCalculator.calculateParkingFee(
            pending.getEntryTime(), confirmationTime, 
            ticket.getSpotType(), ticket.getVehicleType()
        );
        
        double currentFines = calculateFines(ticket, confirmationTime);
        double unpaidFines = getUnpaidFines(licensePlate);
        double currentTotalDue = currentParkingFee + currentFines + unpaidFines;
        
        boolean hasChanged = false;
        
        if (Math.abs(currentParkingFee - pending.getParkingFee()) > 0.01 || currentFines > pending.getCurrentFines()) {
            System.out.println("\nFEES UPDATED due to time elapsed.");
            hasChanged = true;
            
            pending.setParkingFee(currentParkingFee);
            pending.setCurrentFines(currentFines);
            pending.setTotalFines(currentFines + unpaidFines);
            pending.setTotalDue(currentTotalDue);
            pending.setLastCheckedTime(confirmationTime);
            pending.setNextHourThreshold(calculateNextHourThreshold(pending.getEntryTime(), confirmationTime));
            
            System.out.println("   New Total Due: RM " + String.format("%.2f", currentTotalDue));
        }
        
        return hasChanged;
    }
    
    // --- STEP 3: CONFIRM EXIT ---
    public Receipt confirmExit(String licensePlate, double parkingFeePayment, 
                              double finePayment, String paymentMethod) {
        
        System.out.println("\n=== Confirming Exit for Vehicle: " + licensePlate + " ===\n");
        
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) {
            System.out.println("ERROR: No pending exit found. Please initiate exit first.");
            return null;
        }
        
        if (checkForUpdates(licensePlate)) {
            System.out.println("\nExit CONFIRMATION BLOCKED - Fees have changed. Review and confirm again.");
            return null;
        }
        
        LocalDateTime confirmationTime = getCurrentTime();
        Ticket ticket = pending.getTicket();
        String spotId = pending.getSpotId();
        
        double parkingFee = pending.getParkingFee();
        double totalFinesAvailable = pending.getTotalFines();
        
        // 1. Process Parking Fee (Must be paid in full)
        double parkingFeePaidAmount = 0.0;
        double change = 0.0;
        
        if (paymentMethod.equalsIgnoreCase("CARD")) {
            if (Math.abs(parkingFeePayment - parkingFee) < 0.01) {
                parkingFeePaidAmount = parkingFee;
            } else {
                System.out.println("Card Payment FAILED: Must pay exact amount.");
                return null;
            }
        } else if (paymentMethod.equalsIgnoreCase("CASH")) {
            if (parkingFeePayment >= parkingFee) {
                parkingFeePaidAmount = parkingFee;
                change = parkingFeePayment - parkingFee;
                System.out.println("Cash Payment Accepted. Change: RM " + String.format("%.2f", change));
            } else {
                System.out.println("Cash Payment FAILED: Insufficient amount.");
                return null;
            }
        } else {
            System.out.println("Invalid Payment Method.");
            return null;
        }
        
        // 2. Process Fines
        double finesPaidNow = 0.0;
        if (finePayment > 0) {
            finesPaidNow = Math.min(finePayment, totalFinesAvailable);
            if (finesPaidNow > 0) {
                // Mark paid fines in DB
                fineDAO.markFinesAsPaid(licensePlate); // Simplification: Clears all if paying full
                System.out.println("Fines Paid: RM " + String.format("%.2f", finesPaidNow));
            }
        }
        
        // 3. Handle Remaining Fines (New fines + Unpaid balance)
        double remainingFines = totalFinesAvailable - finesPaidNow;
        if (remainingFines > 0) {
            // Add any NEWLY accrued fines to the DB if they weren't paid
            // (Existing unpaid fines are already in DB, so we don't re-add them unless we clear and re-add)
            // Strategy: We only add the *Current Session* fines if they weren't paid.
            double newFines = pending.getCurrentFines();
            if (newFines > 0 && finesPaidNow < totalFinesAvailable) {
                 fineDAO.createFine(licensePlate, newFines, "Overstay/Violation");
            }
            System.out.println("Outstanding Fines Recorded: RM " + String.format("%.2f", remainingFines));
        }

        // 4. Cleanup & Save
        ParkingLot.getInstance().setSpotStatus(spotId, ParkingSpot.Status.AVAILABLE);
        ParkingLot.getInstance().removeTicket(licensePlate);
        
        pendingExits.remove(licensePlate);
        
        double totalPaid = parkingFeePaidAmount + finesPaidNow;
        
        // 5. Generate Receipt
        Receipt receipt = new Receipt(
            licensePlate,
            ticket.getEntryTime(),
            confirmationTime,
            spotId,
            ticket.getSpotType(),
            ticket.getVehicleType(),
            calculateHoursParked(ticket.getEntryTime(), confirmationTime),
            parkingFee,
            finesPaidNow,
            totalFinesAvailable, // Total Outstanding
            parkingFeePaidAmount,
            finesPaidNow,
            totalPaid,
            change,
            paymentMethod,
            ticket.getTicketID(),
            true
        );
        
        // 6. Save Receipt to DB
        receiptDAO.create(receipt);
        
        System.out.println("\nVehicle exit completed successfully!");
        return receipt;
    }

    // --- HELPER METHODS ---
    
    public Receipt processExit(String licensePlate, double amountPaid, String paymentMethod) {
        PendingExit pending = initiateExit(licensePlate);
        if (pending == null) return null;
        return confirmExit(licensePlate, pending.getParkingFee(), 0.0, paymentMethod); // Default: Pay only parking
    }
    
    public Receipt processExit(String licensePlate) {
        PendingExit pending = initiateExit(licensePlate);
        if (pending == null) return null;
        return confirmExit(licensePlate, pending.getParkingFee(), 0.0, "CASH");
    }

    private LocalDateTime calculateNextHourThreshold(LocalDateTime entryTime, LocalDateTime currentTime) {
        Duration duration = Duration.between(entryTime, currentTime);
        long hoursParked = (long) Math.ceil(duration.toMinutes() / 60.0);
        return entryTime.plusMinutes(hoursParked * 60);
    }

    private String parseSpotId(String spotId) {
        // (Keep your parsing logic here)
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

    private double getUnpaidFines(String licensePlate) {
        return fineDAO.getUnpaidFinesAmount(licensePlate);
    }

    public void setFineScheme(FineScheme scheme) {
        fineManager.setFineScheme(scheme);
    }
    
    // Inner Classes (PendingExit, ExitRecord) - Keep exactly as you wrote them
    public static class PendingExit {
        // Copy your PendingExit code here exactly
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
       // Copy your ExitRecord code here if you use it for reporting
    }
}
