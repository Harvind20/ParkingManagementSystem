package ExitModule;

import EntryModule.Ticket;
import FineModule.FineManager;
import FineModule.FineScheme;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExitSystem {
    private FeeCalculator feeCalculator;
    private FineManager fineManager;
    private Map<String, Double> unpaidFinesDatabase;
    private Map<String, ExitRecord> exitRecords;
    private Map<String, PendingExit> pendingExits;
    
    // For testing purposes - allows simulating different times
    private LocalDateTime testTime = null;
    
    public ExitSystem() {
        this.feeCalculator = new FeeCalculator();
        this.fineManager = new FineManager();
        this.unpaidFinesDatabase = new HashMap<>();
        this.exitRecords = new HashMap<>();
        this.pendingExits = new HashMap<>();
        
        // Initialize test unpaid fines
        unpaidFinesDatabase.put("ABC1234", 15.0);
        unpaidFinesDatabase.put("REPEAT-OFFENDER", 75.0);
    }
    
    /**
     * FOR TESTING ONLY - Set a specific time for simulation
     */
    public void setTestTime(LocalDateTime time) {
        this.testTime = time;
        System.out.println("[TEST] Time set to: " + time);
    }
    
    /**
     * FOR TESTING ONLY - Clear test time and return to real time
     */
    public void clearTestTime() {
        this.testTime = null;
        System.out.println("[TEST] Returned to real system time");
    }
    
    /**
     * Get current time (uses test time if set, otherwise real time)
     */
    private LocalDateTime getCurrentTime() {
        if (testTime != null) {
            return testTime;
        }
        return LocalDateTime.now();
    }
    
    /**
     * Step 1: Customer initiates exit - Show current fee calculation
     */
    public PendingExit initiateExit(String licensePlate) {
        System.out.println("\n=== Initiating Exit for Vehicle: " + licensePlate + " ===\n");
        
        Ticket ticket = ParkingLot.getInstance().getTicketByPlate(licensePlate);
        
        if (ticket == null) {
            System.out.println("ERROR: No active ticket found for license plate: " + licensePlate);
            return null;
        }
        
        String spotId = parseSpotId(ticket.getSpotId());
        if (spotId == null) {
            System.out.println("ERROR: Invalid spot ID format: " + ticket.getSpotId());
            return null;
        }
        
        LocalDateTime entryTime = ticket.getEntryTime();
        LocalDateTime initiationTime = getCurrentTime();
        
        double parkingFee = feeCalculator.calculateParkingFee(
            entryTime, initiationTime, ticket.getSpotType(), ticket.getVehicleType()
        );
        
        double currentFines = calculateFines(ticket, initiationTime);
        double unpaidFines = getUnpaidFines(licensePlate);
        double totalFines = currentFines + unpaidFines;
        double totalDue = parkingFee + totalFines;
        
        LocalDateTime nextHourThreshold = calculateNextHourThreshold(entryTime, initiationTime);
        LocalDateTime twentyFourHourThreshold = entryTime.plusHours(24);
        
        System.out.println("Exit initiated successfully");
        System.out.println("   Initiation Time: " + initiationTime);
        System.out.println("   Parking Fee (at initiation): RM " + String.format("%.2f", parkingFee));
        System.out.println("   Total Due (at initiation): RM " + String.format("%.2f", totalDue));
        System.out.println("   Next hour threshold: " + nextHourThreshold);
        System.out.println("   24-hour threshold: " + twentyFourHourThreshold);
        System.out.println("   If you confirm after " + nextHourThreshold + ", fee will increase");
        System.out.println("   If you confirm after " + twentyFourHourThreshold + ", fine will be added");
        
        PendingExit pending = new PendingExit(
            licensePlate,
            ticket,
            spotId,
            entryTime,
            initiationTime,
            initiationTime,
            parkingFee,
            currentFines,
            unpaidFines,
            totalFines,
            totalDue,
            nextHourThreshold,
            twentyFourHourThreshold
        );
        
        pendingExits.put(licensePlate, pending);
        return pending;
    }
    
    /**
     * Step 2: Check if fee has changed since initiation
     */
    public boolean checkForUpdates(String licensePlate) {
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) {
            System.out.println("ERROR: No pending exit found for: " + licensePlate);
            return false;
        }
        
        LocalDateTime confirmationTime = getCurrentTime();
        LocalDateTime initiationTime = pending.getInitiatedTime();
        
        long secondsElapsed = Duration.between(initiationTime, confirmationTime).toSeconds();
        if (secondsElapsed < 1) {
            System.out.println("No time elapsed since initiation");
            return false;
        }
        
        System.out.println("\nTime elapsed since initiation: " + 
            Duration.between(initiationTime, confirmationTime).toMinutes() + " minutes");
        
        Ticket ticket = pending.getTicket();
        
        double currentParkingFee = feeCalculator.calculateParkingFee(
            pending.getEntryTime(), confirmationTime, 
            ticket.getSpotType(), ticket.getVehicleType()
        );
        
        double currentFines = calculateFines(ticket, confirmationTime);
        double unpaidFines = getUnpaidFines(licensePlate);
        double currentTotalDue = currentParkingFee + currentFines + unpaidFines;
        
        boolean hasChanged = false;
        
        if (Math.abs(currentParkingFee - pending.getParkingFee()) > 0.01) {
            System.out.println("\nPARKING FEE UPDATED");
            System.out.println("   At initiation (" + initiationTime + "): RM " + 
                String.format("%.2f", pending.getParkingFee()));
            System.out.println("   At confirm (" + confirmationTime + "): RM " + 
                String.format("%.2f", currentParkingFee));
            System.out.println("   Increase: RM " + 
                String.format("%.2f", currentParkingFee - pending.getParkingFee()));
            hasChanged = true;
        }
        
        if (currentFines > pending.getCurrentFines()) {
            System.out.println("\nFINES ADDED - Overstay detected");
            System.out.println("   At initiation (" + initiationTime + "): RM " + 
                String.format("%.2f", pending.getCurrentFines()));
            System.out.println("   At confirm (" + confirmationTime + "): RM " + 
                String.format("%.2f", currentFines));
            System.out.println("   Additional fine: RM " + 
                String.format("%.2f", currentFines - pending.getCurrentFines()));
            hasChanged = true;
        }
        
        if (hasChanged) {
            pending.setParkingFee(currentParkingFee);
            pending.setCurrentFines(currentFines);
            pending.setTotalFines(currentFines + unpaidFines);
            pending.setTotalDue(currentTotalDue);
            pending.setLastCheckedTime(confirmationTime);
            pending.setNextHourThreshold(calculateNextHourThreshold(pending.getEntryTime(), confirmationTime));
            
            System.out.println("\nPLEASE REVIEW UPDATED CHARGES");
            System.out.println("   New Total Due: RM " + String.format("%.2f", currentTotalDue));
        } else {
            System.out.println("Fees verified - No changes since initiation");
        }
        
        return hasChanged;
    }
    
    /**
     * Step 3: Confirm and process exit with payment
     */
    public Receipt confirmExit(String licensePlate, double parkingFeePayment, 
                              double finePayment, String paymentMethod) {
        
        System.out.println("\n=== Confirming Exit for Vehicle: " + licensePlate + " ===\n");
        
        PendingExit pending = pendingExits.get(licensePlate);
        if (pending == null) {
            System.out.println("ERROR: No pending exit found. Please initiate exit first.");
            return null;
        }
        
        boolean hasUpdates = checkForUpdates(licensePlate);
        
        if (hasUpdates) {
            System.out.println("\nExit CONFIRMATION BLOCKED - Fees have changed");
            System.out.println("   Please review the updated charges and confirm again.");
            System.out.println("   Your pending exit has been updated with the new fees.");
            return null;
        }
        
        LocalDateTime confirmationTime = getCurrentTime();
        Ticket ticket = pending.getTicket();
        String spotId = pending.getSpotId();
        LocalDateTime entryTime = pending.getEntryTime();
        
        double parkingFee = pending.getParkingFee();
        double currentFines = pending.getCurrentFines();
        double unpaidFinesBalance = pending.getUnpaidFines();
        double totalFinesAvailable = currentFines + unpaidFinesBalance;
        
        System.out.println("Fees verified - No changes since initiation");
        System.out.println("   Initiation Time: " + pending.getInitiatedTime());
        System.out.println("   Confirmation Time: " + confirmationTime);
        System.out.println("   Parking Fee: RM " + String.format("%.2f", parkingFee));
        System.out.println("   Total Due: RM " + String.format("%.2f", pending.getTotalDue()));
        
        double change = 0.0;
        double parkingFeePaidAmount = 0.0;
        
        // Process parking fee payment (MANDATORY)
        if (paymentMethod.equalsIgnoreCase("CARD")) {
            if (Math.abs(parkingFeePayment - parkingFee) < 0.01) {
                System.out.println("\nCard Payment: RM " + String.format("%.2f", parkingFee));
                parkingFeePaidAmount = parkingFee;
            } else {
                System.out.println("\nCard Payment FAILED: Must pay exact amount");
                System.out.println("   Required: RM " + String.format("%.2f", parkingFee));
                System.out.println("   Received: RM " + String.format("%.2f", parkingFeePayment));
                return null;
            }
        } else if (paymentMethod.equalsIgnoreCase("CASH")) {
            if (parkingFeePayment >= parkingFee) {
                System.out.println("\nCash Payment: RM " + String.format("%.2f", parkingFeePayment));
                parkingFeePaidAmount = parkingFee;
                change = parkingFeePayment - parkingFee;
                if (change > 0) {
                    System.out.println("   Change: RM " + String.format("%.2f", change));
                }
            } else {
                System.out.println("\nCash Payment FAILED: Insufficient amount");
                System.out.println("   Required: RM " + String.format("%.2f", parkingFee));
                System.out.println("   Shortfall: RM " + String.format("%.2f", parkingFee - parkingFeePayment));
                return null;
            }
        } else {
            System.out.println("\nInvalid payment method: " + paymentMethod);
            System.out.println("Accepted methods: CASH or CARD");
            return null;
        }
        
        double finesPaidNow = 0.0;
        double remainingFines = totalFinesAvailable;
        
        if (finePayment > 0) {
            if (finePayment >= totalFinesAvailable) {
                finesPaidNow = totalFinesAvailable;
                remainingFines = 0.0;
                System.out.println("\nFull fine payment: RM " + String.format("%.2f", finesPaidNow));
                clearUnpaidFines(licensePlate);
            } else {
                finesPaidNow = finePayment;
                remainingFines = totalFinesAvailable - finesPaidNow;
                System.out.println("\nPartial fine payment: RM " + String.format("%.2f", finesPaidNow));
                addUnpaidFines(licensePlate, remainingFines);
            }
        } else {
            System.out.println("\nNo fines paid - will be carried to next visit");
            if (currentFines > 0 || unpaidFinesBalance > 0) {
                addUnpaidFines(licensePlate, totalFinesAvailable);
                remainingFines = totalFinesAvailable;
            }
        }
        
        ParkingLot.getInstance().setSpotStatus(spotId, ParkingSpot.Status.AVAILABLE);
        System.out.println("\nSpot " + spotId + " marked as AVAILABLE");
        
        ParkingLot.getInstance().removeTicket(licensePlate);
        System.out.println("Ticket " + ticket.getTicketID() + " closed");
        
        double totalPaid = parkingFeePaidAmount + finesPaidNow;
        LocalDateTime exitTime = confirmationTime;
        double hoursParked = calculateHoursParked(entryTime, exitTime);
        
        ExitRecord record = new ExitRecord(
            licensePlate,
            ticket.getVehicleType(),
            spotId,
            ticket.getSpotType(),
            entryTime,
            exitTime,
            hoursParked,
            parkingFee,
            finesPaidNow,
            remainingFines,
            totalPaid,
            paymentMethod
        );
        exitRecords.put(licensePlate + "-" + exitTime.toString(), record);
        
        pendingExits.remove(licensePlate);
        
        Receipt receipt = new Receipt(
            licensePlate, 
            entryTime, 
            exitTime,
            spotId, 
            ticket.getSpotType(), 
            ticket.getVehicleType(),
            hoursParked, 
            parkingFee, 
            finesPaidNow,
            totalFinesAvailable,
            parkingFeePaidAmount,
            finesPaidNow,
            totalPaid,
            change,
            paymentMethod,
            ticket.getTicketID(),
            true
        );
        
        System.out.println("\nVehicle exit completed successfully!");
        return receipt;
    }
    
    /**
     * Overloaded method - pay parking fee only, no fines
     */
    public Receipt processExit(String licensePlate, double amountPaid, String paymentMethod) {
        PendingExit pending = initiateExit(licensePlate);
        if (pending == null) return null;
        return confirmExit(licensePlate, amountPaid, 0.0, paymentMethod);
    }
    
    /**
     * Auto-calculate with no fine payment
     */
    public Receipt processExit(String licensePlate) {
        PendingExit pending = initiateExit(licensePlate);
        if (pending == null) return null;
        return confirmExit(licensePlate, pending.getParkingFee(), 0.0, "CASH");
    }
    
    /**
     * Calculate next hour threshold
     */
    private LocalDateTime calculateNextHourThreshold(LocalDateTime entryTime, LocalDateTime currentTime) {
        Duration duration = Duration.between(entryTime, currentTime);
        long totalMinutes = duration.toMinutes();
        long hoursParked = (long) Math.ceil(totalMinutes / 60.0);
        return entryTime.plusMinutes(hoursParked * 60);
    }
    
    /**
     * Parse Spot ID from "F1-R1-S5" format to "1-1-5" format
     */
    private String parseSpotId(String spotId) {
        if (spotId == null) return null;
        
        try {
            if (spotId.contains("F") || spotId.contains("R") || spotId.contains("S")) {
                String[] parts = spotId.split("-");
                if (parts.length == 3) {
                    String floor = parts[0].replaceAll("[^0-9]", "");
                    String row = parts[1].replaceAll("[^0-9]", "");
                    String spot = parts[2].replaceAll("[^0-9]", "");
                    return floor + "-" + row + "-" + spot;
                }
            }
            return spotId;
        } catch (Exception e) {
            System.out.println("Error parsing spot ID: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Calculate fines using Thassveen's Bridge Pattern
     */
    private double calculateFines(Ticket ticket, LocalDateTime exitTime) {
        double hoursParked = calculateHoursParked(ticket.getEntryTime(), exitTime);
        if (hoursParked <= 24) return 0.0;
        return fineManager.calculateFine((int) hoursParked);
    }
    
    /**
     * Calculate hours parked (rounded UP to nearest hour)
     */
    private double calculateHoursParked(LocalDateTime entryTime, LocalDateTime exitTime) {
        Duration duration = Duration.between(entryTime, exitTime);
        double totalMinutes = duration.toMinutes();
        if (totalMinutes <= 0) return 1.0;
        return Math.ceil(totalMinutes / 60.0);
    }
    
    /**
     * Get unpaid fines from database
     */
    private double getUnpaidFines(String licensePlate) {
        return unpaidFinesDatabase.getOrDefault(licensePlate, 0.0);
    }
    
    /**
     * Add unpaid fines to database
     */
    private void addUnpaidFines(String licensePlate, double amount) {
        if (amount > 0.01) {
            double current = unpaidFinesDatabase.getOrDefault(licensePlate, 0.0);
            unpaidFinesDatabase.put(licensePlate, current + amount);
            System.out.println("Unpaid fines recorded for " + licensePlate + 
                ": RM " + String.format("%.2f", current + amount));
        }
    }
    
    /**
     * Clear unpaid fines
     */
    private void clearUnpaidFines(String licensePlate) {
        unpaidFinesDatabase.remove(licensePlate);
        System.out.println("Unpaid fines cleared for " + licensePlate);
    }
    
    /**
     * Set fine scheme (for Admin)
     */
    public void setFineScheme(FineScheme scheme) {
        fineManager.setFineScheme(scheme);
        System.out.println("Fine scheme changed to: " + scheme.getClass().getSimpleName());
    }
    
    /**
     * TEST METHOD ONLY - Adds unpaid fines for testing purposes
     */
    public void addTestUnpaidFines(String licensePlate, double amount) {
        if (this.unpaidFinesDatabase == null) {
            this.unpaidFinesDatabase = new HashMap<>();
        }
        this.unpaidFinesDatabase.put(licensePlate, amount);
        System.out.println("[TEST] Added unpaid fines for " + licensePlate + 
            ": RM " + String.format("%.2f", amount));
    }
    
    /**
     * Get exit history for reporting
     */
    public Map<String, ExitRecord> getExitRecords() {
        return exitRecords;
    }
    
    /**
     * PENDING EXIT CLASS - Stores exit session state
     */
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
        
        // Getters
        public String getLicensePlate() { return licensePlate; }
        public Ticket getTicket() { return ticket; }
        public String getSpotId() { return spotId; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public LocalDateTime getInitiatedTime() { return initiatedTime; }
        public LocalDateTime getLastCheckedTime() { return lastCheckedTime; }
        public double getParkingFee() { return parkingFee; }
        public double getCurrentFines() { return currentFines; }
        public double getUnpaidFines() { return unpaidFines; }
        public double getTotalFines() { return totalFines; }
        public double getTotalDue() { return totalDue; }
        public LocalDateTime getNextHourThreshold() { return nextHourThreshold; }
        public LocalDateTime getTwentyFourHourThreshold() { return twentyFourHourThreshold; }
        
        // Setters for updates
        public void setParkingFee(double parkingFee) { this.parkingFee = parkingFee; }
        public void setCurrentFines(double currentFines) { this.currentFines = currentFines; }
        public void setTotalFines(double totalFines) { this.totalFines = totalFines; }
        public void setTotalDue(double totalDue) { this.totalDue = totalDue; }
        public void setLastCheckedTime(LocalDateTime time) { this.lastCheckedTime = time; }
        public void setNextHourThreshold(LocalDateTime threshold) { this.nextHourThreshold = threshold; }
    }
    
    /**
     * EXIT RECORD CLASS - Stores exit history for reporting
     */
    public static class ExitRecord {
        private String licensePlate;
        private String vehicleType;
        private String spotId;
        private String spotType;
        private LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private double hoursParked;
        private double parkingFee;
        private double finesPaid;
        private double remainingFines;
        private double totalPaid;
        private String paymentMethod;
        
        public ExitRecord(String licensePlate, String vehicleType, String spotId, 
                         String spotType, LocalDateTime entryTime, LocalDateTime exitTime,
                         double hoursParked, double parkingFee, double finesPaid,
                         double remainingFines, double totalPaid, String paymentMethod) {
            this.licensePlate = licensePlate;
            this.vehicleType = vehicleType;
            this.spotId = spotId;
            this.spotType = spotType;
            this.entryTime = entryTime;
            this.exitTime = exitTime;
            this.hoursParked = hoursParked;
            this.parkingFee = parkingFee;
            this.finesPaid = finesPaid;
            this.remainingFines = remainingFines;
            this.totalPaid = totalPaid;
            this.paymentMethod = paymentMethod;
        }
        
        // Getters for reporting
        public LocalDateTime getExitTime() { return exitTime; }
        public String getLicensePlate() { return licensePlate; }
        public double getTotalPaid() { return totalPaid; }
        public double getRemainingFines() { return remainingFines; }
        
        @Override
        public String toString() {
            return String.format("ExitRecord[%s, %s, Exit: %s, Paid: RM%.2f, Remaining Fines: RM%.2f]",
                licensePlate, spotId, exitTime, totalPaid, remainingFines);
        }
    }
}