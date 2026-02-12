package EntryModule;

import ExitModule.ExitSystem;
import ExitModule.Receipt;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import FineModule.*;
import java.time.LocalDateTime;

public class IntegrationTest {
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("      MASTER INTEGRATION TEST SUITE      ");
        System.out.println("==================================================");

        testSUVInCompact();
        testHandicappedInRegular();
        testHandicappedInHandicapped();
        testStandardParkingNoFine();
        testFixedFineStrategy();
        testHourlyFineStrategy();
        testProgressiveFineStrategy();
    }

    private static void testSUVInCompact() {
        System.out.println("\n[TEST 1] SUV Parking in Compact Spot (Should Fail)");
        EntryController entry = new EntryController();
        Vehicle suv = new SUV("BIG-SUV-99");
        
        String result = entry.attemptPark(suv, "1-1-4");
        
        if (result.contains("ERROR") && result.contains("SUV")) {
            printPass("System correctly blocked SUV from Compact spot.");
        } else {
            printFail("System ALLOWED SUV in Compact spot! Result: " + result);
        }
    }

    private static void testHandicappedInRegular() {
        System.out.println("\n[TEST 2] Handicapped Vehicle in Regular Spot (Discount: RM 2/hr)");
        
        LocalDateTime fiveHoursAgo = LocalDateTime.now().minusHours(5);
        Ticket t = new Ticket("OKU-DISC", "HandicappedVehicle", "1-1-1", "Regular", fiveHoursAgo);
        ParkingLot.getInstance().saveTicket(t);

        ExitSystem exit = new ExitSystem();
        Receipt receipt = exit.processExit("OKU-DISC", 50.0);

        if (receipt != null && Math.abs(receipt.getTotalDue() - 10.0) < 0.01) {
            printPass("Fee calculated correctly (RM 10.00). Discount applied.");
        } else {
            printFail("Wrong Fee! Expected RM 10.00, Got: " + (receipt == null ? "null" : receipt.getTotalDue()));
        }
    }

    private static void testHandicappedInHandicapped() {
        System.out.println("\n[TEST 3] Handicapped in Handicapped Spot (Should be FREE)");

        LocalDateTime tenHoursAgo = LocalDateTime.now().minusHours(10);
        Ticket t = new Ticket("OKU-FREE", "HandicappedVehicle", "1-1-7", "Handicapped", tenHoursAgo);
        ParkingLot.getInstance().saveTicket(t);

        ExitSystem exit = new ExitSystem();
        Receipt receipt = exit.processExit("OKU-FREE", 0.0);

        if (receipt != null && receipt.getTotalDue() == 0.0) {
            printPass("Parking is FREE as expected.");
        } else {
            printFail("System charged the user! Total: " + (receipt == null ? "null" : receipt.getTotalDue()));
        }
    }

    private static void testStandardParkingNoFine() {
        System.out.println("\n[TEST 4] Standard Car, 2 Hours (No Fine)");

        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);
        Ticket t = new Ticket("MYVI-88", "Car", "1-1-1", "Regular", twoHoursAgo);
        ParkingLot.getInstance().saveTicket(t);

        ExitSystem exit = new ExitSystem();
        Receipt receipt = exit.processExit("MYVI-88", 20.0);

        if (receipt != null && receipt.getTotalDue() == 10.0 && receipt.getFines() == 0.0) {
            printPass("Standard calculation correct (RM 10.00).");
        } else {
            printFail("Calculation error. Total: " + receipt.getTotalDue());
        }
    }

    private static void testFixedFineStrategy() {
        System.out.println("\n[TEST 5] Overstay (25 Hours) - Fixed Fine Strategy");

        LocalDateTime past = LocalDateTime.now().minusHours(25);
        Ticket t = new Ticket("FIXED-F", "Car", "1-1-1", "Regular", past);
        ParkingLot.getInstance().saveTicket(t);

        ExitSystem exit = new ExitSystem();
        Receipt receipt = exit.processExit("FIXED-F", 200.0);

        if (receipt != null && receipt.getFines() == 50.0) {
            printPass("Fixed Fine (RM 50) applied correctly.");
        } else {
            printFail("Wrong Fine! Expected RM 50. Got: " + (receipt == null ? "null" : receipt.getFines()));
        }
    }

    private static void testHourlyFineStrategy() {
        System.out.println("\n[TEST 6] Overstay (26 Hours) - Hourly Fine Strategy");

        // 1. Inject Ticket (26 hours = 2 hours overstay)
        LocalDateTime past = LocalDateTime.now().minusHours(26);
        Ticket t = new Ticket("HOUR-F", "Car", "1-1-2", "Regular", past);
        ParkingLot.getInstance().saveTicket(t);

        FineManager fm = new FineManager();
        fm.setFineScheme(new HourlyFine());
        double fine = fm.calculateFine(26); // 2 hours over * 20
        
        if (fine == 40.0) {
            printPass("Hourly Fine Logic Correct (2 hours over * RM 20 = RM 40).");
        } else {
            printFail("Hourly logic failed. Got: " + fine);
        }
    }

    private static void testProgressiveFineStrategy() {
        System.out.println("\n[TEST 7] Extreme Overstay (50 Hours) - Progressive Fine");

        FineManager fm = new FineManager();
        fm.setFineScheme(new ProgressiveFine());
        
        double fine = fm.calculateFine(50); 
        
        if (fine == 150.0) {
            printPass("Progressive Fine Logic Correct (Tier 2 = RM 150).");
        } else {
            printFail("Progressive logic failed. Expected 150. Got: " + fine);
        }
    }

    private static void printPass(String msg) {
        System.out.println(ANSI_GREEN + "✔ PASS: " + msg + ANSI_RESET);
    }

    private static void printFail(String msg) {
        System.out.println(ANSI_RED + "✘ FAIL: " + msg + ANSI_RESET);
    }
}