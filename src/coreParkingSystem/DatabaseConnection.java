package coreParkingSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:parking.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            if (conn != null) {
                Statement stmt = conn.createStatement();
                stmt.execute("PRAGMA foreign_keys = ON;");
                stmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
        return conn;
    }
    public static void initializeDB() {
        String sqlSpots = "CREATE TABLE IF NOT EXISTS parking_spots ("
                + " spot_id TEXT PRIMARY KEY,"
                + " type TEXT NOT NULL,"
                + " status TEXT NOT NULL"
                + ");";
        String sqlVehicles = "CREATE TABLE IF NOT EXISTS vehicles ("
                + " plate_num TEXT PRIMARY KEY,"
                + " type TEXT NOT NULL,"
                + " is_vip BOOLEAN DEFAULT 0"
                + ");";
        String sqlTickets = "CREATE TABLE IF NOT EXISTS tickets ("
                + " ticket_id TEXT PRIMARY KEY,"
                + " plate_num TEXT NOT NULL,"
                + " spot_id TEXT NOT NULL,"
                + " entry_time TEXT NOT NULL,"
                + " FOREIGN KEY (plate_num) REFERENCES vehicles(plate_num),"
                + " FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id)"
                + ");";
        String sqlFines = "CREATE TABLE IF NOT EXISTS fines ("
                + " fine_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " plate_num TEXT NOT NULL,"
                + " amount REAL NOT NULL,"
                + " reason TEXT NOT NULL,"
                + " status TEXT NOT NULL,"
                + " FOREIGN KEY (plate_num) REFERENCES vehicles(plate_num)"
                + ");";
        String sqlReceipts = "CREATE TABLE IF NOT EXISTS receipts ("
                + " receipt_id TEXT PRIMARY KEY,"
                + " ticket_id TEXT NOT NULL,"
                + " plate_num TEXT NOT NULL,"
                + " spot_id TEXT NOT NULL,"
                + " entry_time TEXT NOT NULL,"
                + " exit_time TEXT NOT NULL,"
                + " hours_parked REAL NOT NULL,"
                + " parking_fee REAL NOT NULL,"
                + " fine_amount REAL NOT NULL,"
                + " total_paid REAL NOT NULL,"
                + " payment_method TEXT NOT NULL,"
                + " FOREIGN KEY (ticket_id) REFERENCES tickets(ticket_id)"
                + ");";
        String sqlAdmin = "CREATE TABLE IF NOT EXISTS admin_settings ("
                + " setting_key TEXT PRIMARY KEY,"
                + " setting_value TEXT NOT NULL"
                + ");";
        String sqlInsertStrategy = "INSERT OR IGNORE INTO admin_settings (setting_key, setting_value) "
                + "VALUES ('fine_strategy', 'FIXED');";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlSpots);
            stmt.execute(sqlVehicles);
            stmt.execute(sqlTickets);
            stmt.execute(sqlFines);
            stmt.execute(sqlReceipts);
            stmt.execute(sqlAdmin);
            stmt.execute(sqlInsertStrategy);
            
            System.out.println("Database tables initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing Database: " + e.getMessage());
        }
    }
}