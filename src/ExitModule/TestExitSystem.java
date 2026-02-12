package ExitModule;

import EntryModule.*;
import coreParkingSystem.ParkingLot;
import java.time.LocalDateTime;

public class TestExitSystem {
    public static void main(String[] args) {
        // Initialize
        ParkingLot.getInstance();
        EntryController entry = new EntryController();
        ExitSystem exitSystem = new ExitSystem();
        
        System.out.println("==================================================");
        System.out.println("      TESTING EXIT SYSTEM EDGE CASES");
        System.out.println("==================================================\n");
        
        // ============ TEST 1: FEE UPDATE BEFORE CONFIRMATION ============
        System.out.println("\nTEST 1: Fee Update Before Confirmation");
        System.out.println("----------------------------------------");
        System.out.println("Scenario: Customer initiates exit at 1.5 hours,");
        System.out.println("         but confirms at 2.1 hours (fee should increase)");
        System.out.println("----------------------------------------");
        
        // Create vehicle with entry time = 1.5 hours ago
        Vehicle car1 = new Car("EDGE-1");
        LocalDateTime entryTime1 = LocalDateTime.now().minusMinutes(90); // 1.5 hours ago
        try {
            java.lang.reflect.Field entryTimeField = Vehicle.class.getDeclaredField("entryTime");
            entryTimeField.setAccessible(true);
            entryTimeField.set(car1, entryTime1.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        } catch (Exception e) {}
        entry.attemptPark(car1, "1-1-1");
        
        // Step 1: Initiate exit at current time (1.5 hours)
        System.out.println("\n[STEP 1] Initiating exit at 1.5 hours...");
        exitSystem.initiateExit("EDGE-1");
        
        // Step 2: SIMULATE 30 MINUTES PASSING by setting test time
        System.out.println("\n[STEP 2] 30 minutes passes... Customer is slow to pay");
        LocalDateTime simulatedConfirmationTime = LocalDateTime.now().minusMinutes(90).plusMinutes(126); // 2.1 hours
        exitSystem.setTestTime(simulatedConfirmationTime);
        System.out.println("   Simulated confirmation time: " + simulatedConfirmationTime);
        System.out.println("   Time elapsed: 30 minutes");
        
        // Step 3: Customer clicks confirm (should detect fee change)
        System.out.println("\n[STEP 3] Customer clicks 'Confirm & Pay'");
        Receipt r1 = exitSystem.confirmExit("EDGE-1", 10.0, 0.0, "CASH");
        
        if (r1 == null) {
            System.out.println("\nPASS: Transaction blocked - Fee changed");
            System.out.println("   Customer must review new fee");
        } else {
            System.out.println("\nFAIL: Transaction proceeded with old fee!");
        }
        
        // Clear test time
        exitSystem.clearTestTime();
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // ============ TEST 2: 24-HOUR THRESHOLD CROSSED ============
        System.out.println("\nTEST 2: 24-Hour Threshold Crossed");
        System.out.println("----------------------------------------");
        System.out.println("Scenario: Customer initiates exit at 23.8 hours,");
        System.out.println("         but confirms at 24.1 hours (should add fine)");
        System.out.println("----------------------------------------");
        
        // Create vehicle with entry time = 23.8 hours ago
        Vehicle car2 = new Car("EDGE-2");
        LocalDateTime entryTime2 = LocalDateTime.now().minusMinutes((long)(23.8 * 60)); // 23.8 hours
        try {
            java.lang.reflect.Field entryTimeField = Vehicle.class.getDeclaredField("entryTime");
            entryTimeField.setAccessible(true);
            entryTimeField.set(car2, entryTime2.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
        } catch (Exception e) {}
        entry.attemptPark(car2, "1-1-2");
        
        // Step 1: Initiate exit at 23.8 hours
        System.out.println("\n[STEP 1] Initiating exit at 23.8 hours...");
        exitSystem.initiateExit("EDGE-2");
        
        // Step 2: SIMULATE 30 MINUTES PASSING (cross 24 hours)
        System.out.println("\n[STEP 2] 30 minutes passes... Customer is on phone");
        System.out.println("   Now past 24 hours! Overstay fine should trigger");
        LocalDateTime simulatedConfirmationTime2 = LocalDateTime.now().minusMinutes((long)(23.8 * 60)).plusMinutes((long)(24.1 * 60));
        exitSystem.setTestTime(simulatedConfirmationTime2);
        System.out.println("   Simulated confirmation time: " + simulatedConfirmationTime2);
        System.out.println("   Time elapsed: 30 minutes");
        
        // Step 3: Customer clicks confirm (should detect fine)
        System.out.println("\n[STEP 3] Customer clicks 'Confirm & Pay'");
        Receipt r2 = exitSystem.confirmExit("EDGE-2", 150.0, 0.0, "CASH");
        
        if (r2 == null) {
            System.out.println("\nPASS: Transaction blocked - Fine added");
            System.out.println("   Customer must review new charges with overstay fine");
        } else {
            System.out.println("\nFAIL: Transaction proceeded without applying fine!");
        }
        
        // Clear test time
        exitSystem.clearTestTime();
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        System.out.println("==================================================");
        System.out.println("            EDGE CASE TESTS COMPLETED");
        System.out.println("==================================================");
    }
}