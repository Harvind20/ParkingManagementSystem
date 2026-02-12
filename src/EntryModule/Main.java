package EntryModule;

import ExitModule.ExitSystem;
import ExitModule.ExitSystem.PendingExit;
import ExitModule.Receipt;
import FineModule.FineManager;
import FineModule.HourlyFine;
import FineModule.ProgressiveFine;
import coreParkingSystem.AdminSettingsDAO;
import coreParkingSystem.DatabaseConnection;
import coreParkingSystem.ParkingLot;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";

    public static void main(String[] args) {
        System.out.println(CYAN + "==========================================");
        System.out.println("   PARKING MANAGEMENT SYSTEM - FULL TEST");
        System.out.println("==========================================" + RESET);
        
        ParkingLot.getInstance();
        seedDatabase();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n" + YELLOW + "--- SELECT USE CASE ---" + RESET);
            System.out.println("1. Driver Entry (Standard)");
            System.out.println("2. Driver Entry (VIP / Reserved)");
            System.out.println("3. Driver Entry (Handicapped)");
            System.out.println("4. Unauthorized Entry (SUV in Compact / Non-VIP in Reserved)");
            System.out.println("5. Exit Process (Standard - Cash/Card)");
            System.out.println("6. Exit Process (Overstay & Fines)");
            System.out.println("7. Admin: Change Fine Strategy");
            System.out.println("8. View Parking Status (Debug)");
            System.out.println("0. Exit Test");
            System.out.print("Select Option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": testStandardEntry(); break;
                case "2": testVIPEntry(); break;
                case "3": testHandicappedEntry(); break;
                case "4": testUnauthorizedEntry(); break;
                case "5": testStandardExit(scanner); break;
                case "6": testOverstayExit(scanner); break;
                case "7": testAdminConfig(scanner); break;
                case "8": printParkingStatus(); break;
                case "0": running = false; break;
                default: System.out.println(RED + "Invalid Option" + RESET);
            }
        }
        scanner.close();
    }

    private static void testStandardEntry() {
        System.out.println("\n" + BLUE + "--- [TEST] STANDARD DRIVER ENTRY ---" + RESET);
        EntryController entry = new EntryController();
        
        Vehicle car = new Car("ABC-1234"); 
        
        System.out.println("Attempting to park Car 'ABC-1234' in Spot 1-1-1...");
        String result = entry.attemptPark(car, "1-1-1");
        
        System.out.println("Result: " + (result.startsWith("SUCCESS") ? GREEN + result : RED + result) + RESET);
    }

    private static void testVIPEntry() {
        System.out.println("\n" + BLUE + "--- [TEST] VIP DRIVER ENTRY ---" + RESET);
        EntryController entry = new EntryController();
        
        Vehicle vipCar = new Car("VIP-999"); 
        
        System.out.println("Attempting to park VIP Car 'VIP-999' in Reserved Spot 1-1-9...");
        String result = entry.attemptPark(vipCar, "1-1-9");
        
        System.out.println("Result: " + (result.startsWith("SUCCESS") ? GREEN + result : RED + result) + RESET);
    }

    private static void testHandicappedEntry() {
        System.out.println("\n" + BLUE + "--- [TEST] HANDICAPPED ENTRY ---" + RESET);
        EntryController entry = new EntryController();
        
        Vehicle okuVeh = new HandicappedVehicle("OKU-888");
        
        System.out.println("Attempting to park 'OKU-888' in Handicapped Spot 1-1-7...");
        String result = entry.attemptPark(okuVeh, "1-1-7");
        
        System.out.println("Result: " + (result.startsWith("SUCCESS") ? GREEN + result : RED + result) + RESET);
    }

    private static void testUnauthorizedEntry() {
        System.out.println("\n" + BLUE + "--- [TEST] UNAUTHORIZED ENTRY SCENARIOS ---" + RESET);
        EntryController entry = new EntryController();

        System.out.println("A. Attempting to park SUV 'BIG-SUV' in Compact Spot 1-1-4...");
        Vehicle suv = new SUV("BIG-SUV");
        String res1 = entry.attemptPark(suv, "1-1-4");
        System.out.println("   Result: " + (res1.contains("ERROR") ? GREEN + "Blocked (Correct)" : RED + "Allowed (Fail)") + " -> " + res1);

        System.out.println("B. Attempting to park Regular Car 'ABC-1234' in Reserved Spot 1-1-10...");
        Vehicle regCar = new Car("ABC-1234");
        String res2 = entry.attemptPark(regCar, "1-1-10");
        System.out.println("   Result: " + (res2.contains("ERROR") ? GREEN + "Blocked (Correct)" : RED + "Allowed (Fail)") + " -> " + res2);
    }

    private static void testStandardExit(Scanner scanner) {
        System.out.println("\n" + BLUE + "--- [TEST] STANDARD EXIT PROCESS ---" + RESET);
        ExitSystem exit = new ExitSystem();
        String plate = "ABC-1234";

        PendingExit pending = exit.initiateExit(plate);
        if (pending == null) {
            System.out.println(RED + "Vehicle not found or not parked." + RESET);
            return;
        }

        System.out.println("\nSimulating Payment...");
        System.out.print("Select Payment Method (CASH/CARD): ");
        String method = scanner.nextLine().toUpperCase();
        
        System.out.print("Enter Amount to Pay (Fee is " + pending.getParkingFee() + "): ");
        double amount = Double.parseDouble(scanner.nextLine());

        Receipt receipt = exit.confirmExit(plate, amount, 0.0, method);
        
        if (receipt != null) {
            System.out.println(GREEN + "\n>>> RECEIPT GENERATED <<<" + RESET);
            System.out.println(receipt.toString());
        } else {
            System.out.println(RED + "Exit Failed (Payment issue or timeout)." + RESET);
        }
    }

    private static void testOverstayExit(Scanner scanner) {
        System.out.println("\n" + BLUE + "--- [TEST] EXIT WITH OVERSTAY & FINES ---" + RESET);
        
        EntryController entry = new EntryController();
        Vehicle v = new Car("LATE-001");
        entry.attemptPark(v, "1-1-2");

        hackTimeTravel("LATE-001", 50); 
        System.out.println(YELLOW + "[System] Simulating 50-hour parking duration..." + RESET);

        ExitSystem exit = new ExitSystem();
        PendingExit pending = exit.initiateExit("LATE-001");
        
        if (pending == null) return;

        System.out.println(RED + "Notice: You have Fines!" + RESET);
        System.out.println("Parking Fee: " + pending.getParkingFee());
        System.out.println("Fines: " + pending.getCurrentFines());
        System.out.println("Total Due: " + pending.getTotalDue());

        System.out.print("\nEnter Payment for FEES (Amount): ");
        double feePay = Double.parseDouble(scanner.nextLine());
        
        System.out.print("Enter Payment for FINES (Amount or 0 to skip): ");
        double finePay = Double.parseDouble(scanner.nextLine());

        Receipt receipt = exit.confirmExit("LATE-001", feePay, finePay, "CARD");
        
        if (receipt != null) {
            System.out.println(receipt.toString());
        }
    }

    private static void testAdminConfig(Scanner scanner) {
        System.out.println("\n" + BLUE + "--- [ADMIN] CHANGE FINE STRATEGY ---" + RESET);
        AdminSettingsDAO dao = new AdminSettingsDAO();
        System.out.println("Current Strategy: " + GREEN + dao.getCurrentStrategy() + RESET);

        System.out.println("Select New Strategy:");
        System.out.println("1. FIXED (RM 50 flat)");
        System.out.println("2. HOURLY (RM 20 per hour overstay)");
        System.out.println("3. PROGRESSIVE (Tiered fines)");
        System.out.print("Choice: ");
        
        String choice = scanner.nextLine();
        String newStrat = "FIXED";
        
        if (choice.equals("2")) newStrat = "HOURLY";
        if (choice.equals("3")) newStrat = "PROGRESSIVE";

        dao.setStrategy(newStrat);
        
        if (newStrat.equals("HOURLY")) FineManager.setFineScheme(new HourlyFine());
        else if (newStrat.equals("PROGRESSIVE")) FineManager.setFineScheme(new ProgressiveFine());
        
        System.out.println(GREEN + "Strategy Updated to " + newStrat + RESET);
    }

    private static void printParkingStatus() {
        System.out.println("Use SQL query or check 'parking_FINAL.db' to verify tables.");
    }

    private static void seedDatabase() {
        try (Connection conn = DriverManager.getConnection(DatabaseConnection.URL);
             Statement stmt = conn.createStatement()) {
             
            stmt.execute("DELETE FROM tickets");
            stmt.execute("DELETE FROM receipts");
            stmt.execute("DELETE FROM fines");
            stmt.execute("UPDATE parking_spots SET status='AVAILABLE'");

            stmt.execute("INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES('ABC-1234', 'Car', 0)");
            stmt.execute("INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES('VIP-999', 'Car', 1)");
            stmt.execute("INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES('OKU-888', 'HandicappedVehicle', 0)");
            stmt.execute("INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES('BIG-SUV', 'SUV', 0)");
            stmt.execute("INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES('LATE-001', 'Car', 0)");

            stmt.execute("INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-1', 'REGULAR', 'AVAILABLE')");
            stmt.execute("INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-2', 'REGULAR', 'AVAILABLE')");
            stmt.execute("INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-4', 'COMPACT', 'AVAILABLE')");
            stmt.execute("INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-7', 'HANDICAPPED', 'AVAILABLE')");
            stmt.execute("INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-9', 'RESERVED', 'AVAILABLE')");
            stmt.execute("INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-10', 'RESERVED', 'AVAILABLE')");

        } catch (Exception e) {
            System.out.println("Seed Error: " + e.getMessage());
        }
    }

    private static void hackTimeTravel(String plate, int hoursBack) {
        try (Connection conn = DriverManager.getConnection(DatabaseConnection.URL);
             Statement stmt = conn.createStatement()) {
            String pastTime = LocalDateTime.now().minusHours(hoursBack).toString();
            stmt.execute("UPDATE tickets SET entry_time='" + pastTime + "' WHERE plate_num='" + plate + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}