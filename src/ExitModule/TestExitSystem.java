package ExitModule;

public class TestExitSystem {
    public static void main(String[] args) {
        ExitSystem exitSystem = new ExitSystem();
        
        System.out.println("=== TESTING EXIT SYSTEM WITH FINES ===\n");
        
        // Test 1: Regular car with OVERSTAY (>24 hours)
        System.out.println("Test 1: Regular Car with Overstay (30 hours)");
        System.out.println("Expected: 30 hours × RM5 = RM150 + RM50 fine = RM200");
        System.out.println("-".repeat(40));
        Receipt r1 = exitSystem.processExit("ABC1234", 200.0);
        System.out.println(r1 != null ? r1 : "No receipt");
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Test 2: Handicapped in Handicapped spot = FREE
        System.out.println("Test 2: Handicapped in Handicapped Spot (5 hours)");
        System.out.println("Expected: FREE parking, RM0 fee");
        System.out.println("-".repeat(40));
        Receipt r2 = exitSystem.processExit("HCP7890", 0.0);
        System.out.println(r2 != null ? r2 : "No receipt");
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Test 3: Non-VIP in Reserved spot
        System.out.println("Test 3: Non-VIP in Reserved Spot (3 hours)");
        System.out.println("Expected: 3 hours × RM10 = RM30 + RM100 fine = RM130");
        System.out.println("-".repeat(40));
        Receipt r3 = exitSystem.processExit("NOVIP123", 130.0);
        System.out.println(r3 != null ? r3 : "No receipt");
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Test 4: Auto-calculate payment (handicapped in regular spot)
        System.out.println("Test 4: Auto-calculate - Handicapped in Regular Spot");
        System.out.println("Expected: 3 hours × RM2 = RM6 (no fines)");
        System.out.println("-".repeat(40));
        Receipt r4 = exitSystem.processExit("HCP1111");
        System.out.println(r4 != null ? r4 : "No receipt");
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Test 5: Insufficient payment
        System.out.println("Test 5: Insufficient Payment");
        System.out.println("Expected: Payment should fail, spot remains occupied");
        System.out.println("-".repeat(40));
        Receipt r5 = exitSystem.processExit("VIP9999", 10.0); // Should be RM30
        System.out.println(r5 != null ? r5 : "No receipt");
        
        System.out.println("\n" + "=".repeat(60) + "\n");
        
        // Test 6: Vehicle parked 80 hours (extreme overstay)
        System.out.println("Test 6: Extreme Overstay (80 hours)");
        System.out.println("Expected: 80 hours × RM5 = RM400 + RM50 fine = RM450");
        System.out.println("-".repeat(40));
        Receipt r6 = exitSystem.processExit("OVR72HRS", 450.0);
        System.out.println(r6 != null ? r6 : "No receipt");
    }
}