import coreParkingSystem.ParkingLot;
import EntryModule.Ticket;
import coreParkingSystem.ParkingSpot; // Needed to create spot
import java.time.LocalDateTime;
import java.sql.*; // Needed for direct SQL injection for testing

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("--- TESTING DATABASE CONNECTION ---");

        // 1. Initialize System
        ParkingLot lot = ParkingLot.getInstance();
        System.out.println("Database Initialized.");

        // --- PRE-REQUISITE: DATA SETUP ---
        // We must manually insert the Vehicle and Spot because Ticket has Foreign Keys pointing to them.
        setupTestData(); 
        // ---------------------------------

        // 2. Create a Dummy Ticket
        Ticket t = new Ticket.TicketBuilder()
                .addPlate("DB-TEST-01") // Matches the vehicle we inserted
                .assignSpot("1-1-1")    // Matches the spot we inserted
                .addTime(LocalDateTime.now())
                .build();

        // 3. Save to DB
        try {
            lot.saveTicket(t);
            System.out.println("Ticket Saved.");
        } catch (Exception e) {
            System.out.println("Save Failed: " + e.getMessage());
        }

        // 4. Retrieve from DB
        Ticket retrieved = lot.getTicketByPlate("DB-TEST-01");
        
        if (retrieved != null) {
            System.out.println("SUCCESS: Retrieved Ticket from DB!");
            System.out.println("Plate: " + retrieved.getLicensePlate());
            System.out.println("Time: " + retrieved.getEntryTime());
        } else {
            System.out.println("FAILURE: Could not find ticket in DB.");
        }
    }

    // Helper method to insert parent records so FK constraints don't fail
    private static void setupTestData() {
        String url = "jdbc:sqlite:parking.db";
        String sqlVehicle = "INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES('DB-TEST-01', 'Car', 0)";
        String sqlSpot = "INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES('1-1-1', 'REGULAR', 'AVAILABLE')";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlVehicle);
            stmt.execute(sqlSpot);
            System.out.println("[Test Setup] Vehicle and Spot inserted.");
        } catch (SQLException e) {
            System.out.println("[Test Setup Error] " + e.getMessage());
        }
    }
}