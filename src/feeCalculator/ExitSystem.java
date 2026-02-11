package feeCalculator;

import fineManagement.FineManager;
import java.time.Duration;
import java.time.LocalDateTime;

public class ExitSystem {
    private FeeCalculator feeCalculator;
    
    public ExitSystem() {
        this.feeCalculator = new FeeCalculator();
    }
    
    /**
     * Main facade method - processes vehicle exit
     */
    public Receipt processExit(String licensePlate, double amountPaid) {
        System.out.println("\n=== Processing Exit for Vehicle: " + licensePlate + " ===\n");
        
        // 1. Find the ticket
        Ticket ticket = findTicketByLicensePlate(licensePlate);
        if (ticket == null) {
            System.out.println("Error: No ticket found for license plate: " + licensePlate);
            return null;
        }
        
        // 2. Calculate Fee using simplified calculator
        double parkingFee = feeCalculator.calculateParkingFee(
            ticket.getEntryTime(),
            LocalDateTime.now(),
            ticket.getSpotType(),
            ticket.getVehicleType()
        );
        System.out.println("Parking Fee: RM " + String.format("%.2f", parkingFee));
        
        // 3. Check for fines (this parking session)
        double currentFines = checkForFines(licensePlate, ticket);
        System.out.println("Current Fines: RM " + String.format("%.2f", currentFines));

        // 3B. Check UNPAID fines (previous sessions)
        double unpaidFines = getUnpaidFines(licensePlate);
        System.out.println("Unpaid Fines: RM " + String.format("%.2f", unpaidFines));
        
        // 4. Sum Total
        double totalDue = parkingFee + currentFines + unpaidFines;
        System.out.println("Total Due: RM " + String.format("%.2f", totalDue));
        
        // 5. Process Payment
        boolean paymentSuccess = processPayment(amountPaid, totalDue);
        
        // Create Receipt
        Receipt receipt = createReceipt(ticket, parkingFee, currentFines + unpaidFines, totalDue, amountPaid, paymentSuccess);
        
        // 6. Update spot if payment successful
        if (paymentSuccess) {
            updateSpotStatus(ticket.getSpotId(), "Available");
            System.out.println("Spot " + ticket.getSpotId() + " is now AVAILABLE");
        }
        
        return receipt;
    }
    
    /**
     * Auto-calculate payment
     */
    public Receipt processExit(String licensePlate) {
        Ticket ticket = findTicketByLicensePlate(licensePlate);
        if (ticket == null) return null;
        
        double parkingFee = feeCalculator.calculateParkingFee(
            ticket.getEntryTime(), LocalDateTime.now(),
            ticket.getSpotType(), ticket.getVehicleType()
        );
        double fines = checkForFines(licensePlate, ticket);
        double totalDue = parkingFee + fines;
        
        return processExit(licensePlate, totalDue);
    }
    
    // ========== UPDATED PLACEHOLDER METHODS ==========
    
    private Ticket findTicketByLicensePlate(String licensePlate) {
        System.out.println("Searching ticket for: " + licensePlate);
        
        LocalDateTime now = LocalDateTime.now();
        
        if (licensePlate.equals("ABC1234")) {
            // REGULAR CAR WITH OVERSTAY (>24 hours)
            return new Ticket(
                "ABC1234", "Car", "F1-R1-S5", "Regular",
                now.minusHours(30) // Parked for 30 hours = OVERSTAY!
            );
        } else if (licensePlate.equals("HCP7890")) {
            // HANDICAPPED IN HANDICAPPED SPOT (should be FREE)
            return new Ticket(
                "HCP7890", "HandicappedVehicle", "F1-R2-S1", "Handicapped",
                now.minusHours(5)
            );
        } else if (licensePlate.equals("HCP1111")) {
            // HANDICAPPED IN REGULAR SPOT (should be RM2/hour)
            return new Ticket(
                "HCP1111", "HandicappedVehicle", "F2-R1-S3", "Regular",
                now.minusHours(2).minusMinutes(30)
            );
        } else if (licensePlate.equals("VIP9999")) {
            // VIP IN RESERVED SPOT
            return new Ticket(
                "VIP9999", "Car", "F3-R1-S1", "Reserved",
                now.minusHours(2).minusMinutes(15)
            );
        } else if (licensePlate.equals("NOVIP123")) {
            // NON-VIP IN RESERVED SPOT (should get fine)
            return new Ticket(
                "NOVIP123", "Car", "F3-R2-S1", "Reserved",
                now.minusHours(3)
            );
        } else if (licensePlate.equals("OVR72HRS")) {
            // VEHICLE PARKED FOR 72+ HOURS (progressive fine test)
            return new Ticket(
                "OVR72HRS", "SUV", "F2-R3-S2", "Regular",
                now.minusHours(80) // 80 hours = 3 days + 8 hours
            );
        }
        
        return null;
    }
    
    private double checkForFines(String licensePlate, Ticket ticket) {
        System.out.println("Checking fines...");
        
        double hoursParked = calculateHoursParked(ticket);
        System.out.println("Hours parked: " + String.format("%.1f", hoursParked));
        
        double fines = 0.0;
        
        // 1. OVERSTAY FINE (> 24 hours)
        if (hoursParked > 24) {
            System.out.println("OVERSTAY DETECTED");
            
            FineManager fineManager = new FineManager();

            double overstayFine = fineManager.calculateFine((int) hoursParked);
            fines += overstayFine;

            System.out.println("+ RM " + overstayFine + "overstay fine (Bridge Pattern)");
        }
        
        // 2. RESERVED SPOT VIOLATION
        if (ticket.getSpotType().equals("Reserved")) {
            boolean isVIP = licensePlate.startsWith("VIP");
            boolean isHandicapped = ticket.getVehicleType().equals("HandicappedVehicle");
            
            if (!isVIP && !isHandicapped) {
                System.out.println("RESERVED SPOT VIOLATION: Non-VIP in reserved spot");
                fines += 100.0;
                System.out.println("  + RM100 reserved spot fine");
            }
        }
        
        // 3. COMPACT SPOT VIOLATION (SUV in Compact spot)
        if (ticket.getSpotType().equals("Compact") && 
            (ticket.getVehicleType().equals("SUV") || ticket.getVehicleType().equals("Truck"))) {
            System.out.println("COMPACT SPOT VIOLATION: Large vehicle in compact spot");
            fines += 75.0;
            System.out.println("  + RM75 compact spot fine");
        }
        
        if (fines > 0) {
            System.out.println("Total fines: RM " + String.format("%.2f", fines));
        } else {
            System.out.println("No fines applicable");
        }
        
        return fines;
    }

private double getUnpaidFines(String licensePlate) {
    if (licensePlate.equals("ABC1234")) {
        return 15.0;
    }
    else if (licensePlate.equals("NOVIP123")) {
        return 50.0;
    }
    else if (licensePlate.equals("OVR72HRS")) {
        return 30.0;
    }
    return 0.0;
}
    
    private double calculateHoursParked(Ticket ticket) {
        Duration duration = Duration.between(ticket.getEntryTime(), LocalDateTime.now());
        double totalMinutes = duration.toMinutes();
        return Math.ceil(totalMinutes / 60.0); // Ceiling rounding
    }
    
    private boolean processPayment(double amountPaid, double totalDue) {
        System.out.println("\n💳 Processing Payment...");
        System.out.println("Paid: RM " + String.format("%.2f", amountPaid));
        System.out.println("Due: RM " + String.format("%.2f", totalDue));
        
        if (amountPaid >= totalDue) {
            System.out.println("Payment SUCCESSFUL");
            if (amountPaid > totalDue) {
                System.out.println("Change: RM " + String.format("%.2f", amountPaid - totalDue));
            }
            return true;
        } else {
            System.out.println("Payment FAILED");
            System.out.println("Shortfall: RM " + String.format("%.2f", totalDue - amountPaid));
            return false;
        }
    }
    
    private void updateSpotStatus(String spotId, String status) {
        System.out.println("Updating spot " + spotId + " → " + status);
        // Will call Sanjeevan's method
    }
    
    private Receipt createReceipt(Ticket ticket, double parkingFee, double fines, 
                                 double totalDue, double amountPaid, boolean paymentSuccess) {
        return new Receipt(
            ticket.getLicensePlate(),
            ticket.getEntryTime(),
            LocalDateTime.now(),
            ticket.getSpotId(),
            ticket.getSpotType(),
            ticket.getVehicleType(),
            calculateHoursParked(ticket),
            parkingFee,
            fines,
            totalDue,
            amountPaid,
            paymentSuccess
        );
    }
}

