package ExitModule;

import EntryModule.Ticket;
import FineModule.FineManager;
import coreParkingSystem.ParkingLot;
import java.time.Duration;
import java.time.LocalDateTime;

public class ExitSystem {
    private FeeCalculator feeCalculator;
    
    public ExitSystem() {
        this.feeCalculator = new FeeCalculator();
    }
    
    public Receipt processExit(String licensePlate, double amountPaid) {
        System.out.println("\n=== Processing Exit for Vehicle: " + licensePlate + " ===\n");
        
        Ticket ticket = findTicketByLicensePlate(licensePlate);
        if (ticket == null) {
            System.out.println("Error: No ticket found for license plate: " + licensePlate);
            return null;
        }
        
        double parkingFee = feeCalculator.calculateParkingFee(
            ticket.getEntryTime(),
            LocalDateTime.now(),
            ticket.getSpotType(),
            ticket.getVehicleType()
        );
        System.out.println("Parking Fee: RM " + String.format("%.2f", parkingFee));
        
        double currentFines = checkForFines(licensePlate, ticket);
        System.out.println("Current Fines: RM " + String.format("%.2f", currentFines));

        double unpaidFines = getUnpaidFines(licensePlate);
        System.out.println("Unpaid Fines: RM " + String.format("%.2f", unpaidFines));
        
        double totalDue = parkingFee + currentFines + unpaidFines;
        System.out.println("Total Due: RM " + String.format("%.2f", totalDue));
        
        boolean paymentSuccess = processPayment(amountPaid, totalDue);
        
        Receipt receipt = createReceipt(ticket, parkingFee, currentFines + unpaidFines, totalDue, amountPaid, paymentSuccess);
        
        if (paymentSuccess) {
            updateSpotStatus(ticket.getSpotId(), "Available");

            ParkingLot.getInstance().removeTicket(licensePlate); 
            System.out.println("Spot " + ticket.getSpotId() + " is now AVAILABLE");
        }
        
        return receipt;
    }

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
    
    private Ticket findTicketByLicensePlate(String licensePlate) {
        System.out.println("Searching ticket for: " + licensePlate);
        
        Ticket realTicket = ParkingLot.getInstance().getTicketByPlate(licensePlate);
        
        if (realTicket != null) {
            System.out.println(">> Found Active Ticket in System!");
            return realTicket;
        }

        System.out.println(">> Not in DB. Checking Mock Data...");
        LocalDateTime now = LocalDateTime.now();
        
        if (licensePlate.equals("ABC1234")) {
            return new Ticket("ABC1234", "Car", "F1-R1-S5", "Regular", now.minusHours(30));
        } else if (licensePlate.equals("HCP7890")) {
            return new Ticket("HCP7890", "HandicappedVehicle", "F1-R2-S1", "Handicapped", now.minusHours(5));
        } else if (licensePlate.equals("HCP1111")) {
            return new Ticket("HCP1111", "HandicappedVehicle", "F2-R1-S3", "Regular", now.minusHours(2).minusMinutes(30));
        } else if (licensePlate.equals("VIP9999")) {
            return new Ticket("VIP9999", "Car", "F3-R1-S1", "Reserved", now.minusHours(2).minusMinutes(15));
        } else if (licensePlate.equals("NOVIP123")) {
            return new Ticket("NOVIP123", "Car", "F3-R2-S1", "Reserved", now.minusHours(3));
        } else if (licensePlate.equals("OVR72HRS")) {
            return new Ticket("OVR72HRS", "SUV", "F2-R3-S2", "Regular", now.minusHours(80));
        }
        
        return null;
    }
    
    private double checkForFines(String licensePlate, Ticket ticket) {
        System.out.println("Checking fines...");
        
        double hoursParked = calculateHoursParked(ticket);
        System.out.println("Hours parked: " + String.format("%.1f", hoursParked));
        
        double fines = 0.0;
        
        // 1. OVERSTAY FINE (> 24 hours) - INTEGRATED WITH BRIDGE PATTERN
        // We now ask Thassveen's FineManager to calculate this!
        if (hoursParked > 24) {
            System.out.println("OVERSTAY DETECTED (> 24 hours)");
            
            // Create the manager (Default is FixedFine, but Admin can change it)
            FineManager fineManager = new FineManager(); 
            
            // Calculate fine based on total duration
            double overstayFine = fineManager.calculateFine((int) hoursParked);
            
            fines += overstayFine;
            System.out.println(" + RM " + overstayFine + " overstay fine (Calculated via Bridge Pattern)");
        }
        
        // 2. RESERVED SPOT VIOLATION (Logic stays inside ExitSystem)
        if (ticket.getSpotType().equals("Reserved")) {
            boolean isVIP = licensePlate.startsWith("VIP");
            boolean isHandicapped = ticket.getVehicleType().equals("HandicappedVehicle");
            
            if (!isVIP && !isHandicapped) {
                System.out.println("RESERVED SPOT VIOLATION: Non-VIP in reserved spot");
                fines += 100.0;
                System.out.println(" + RM100 reserved spot fine");
            }
        }
        
        // 3. COMPACT SPOT VIOLATION (Logic stays inside ExitSystem)
        if (ticket.getSpotType().equals("Compact") && 
            (ticket.getVehicleType().equals("SUV") || ticket.getVehicleType().equals("Truck"))) {
            System.out.println("COMPACT SPOT VIOLATION: Large vehicle in compact spot");
            fines += 75.0;
            System.out.println(" + RM75 compact spot fine");
        }
        
        return fines;
    }

    private double getUnpaidFines(String licensePlate) {
        if (licensePlate.equals("ABC1234")) return 15.0;
        return 0.0;
    }
    
    private double calculateHoursParked(Ticket ticket) {
        Duration duration = Duration.between(ticket.getEntryTime(), LocalDateTime.now());
        double totalMinutes = duration.toMinutes();
        return Math.ceil(totalMinutes / 60.0); 
    }
    
    private boolean processPayment(double amountPaid, double totalDue) {
        System.out.println("💳 Processing Payment...");
        System.out.println("Paid: RM " + String.format("%.2f", amountPaid));
        
        if (amountPaid >= totalDue) {
            System.out.println("Payment SUCCESSFUL");
            if (amountPaid > totalDue) {
                System.out.println("Change: RM " + String.format("%.2f", amountPaid - totalDue));
            }
            return true;
        } else {
            System.out.println("Payment FAILED");
            return false;
        }
    }
    
    private void updateSpotStatus(String spotId, String status) {
        coreParkingSystem.ParkingSpot.Status enumStatus = 
            status.equals("Available") ? coreParkingSystem.ParkingSpot.Status.AVAILABLE : coreParkingSystem.ParkingSpot.Status.OCCUPIED;
            
        ParkingLot.getInstance().setSpotStatus(spotId, enumStatus);
    }
    
    private Receipt createReceipt(Ticket ticket, double parkingFee, double fines, 
                                 double totalDue, double amountPaid, boolean paymentSuccess) {
        return new Receipt(
            ticket.getLicensePlate(), ticket.getEntryTime(), LocalDateTime.now(),
            ticket.getSpotId(), ticket.getSpotType(), ticket.getVehicleType(),
            calculateHoursParked(ticket), parkingFee, fines, totalDue, amountPaid, paymentSuccess
        );
    }
}

