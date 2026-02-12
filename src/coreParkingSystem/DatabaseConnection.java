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
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void initializeDB() {
        String[] sqlStatements = {
            "CREATE TABLE IF NOT EXISTS parking_spots (spot_id TEXT PRIMARY KEY, type TEXT, status TEXT)",
            "CREATE TABLE IF NOT EXISTS vehicles (plate_num TEXT PRIMARY KEY, type TEXT)",
            "CREATE TABLE IF NOT EXISTS tickets (ticket_id TEXT PRIMARY KEY, plate_num TEXT, spot_id TEXT, entry_time TEXT)",
            "CREATE TABLE IF NOT EXISTS fines (fine_id INTEGER PRIMARY KEY AUTOINCREMENT, plate_num TEXT, amount REAL, reason TEXT, status TEXT)",
            "CREATE TABLE IF NOT EXISTS receipts (receipt_id TEXT PRIMARY KEY, ticket_id TEXT, plate_num TEXT, spot_id TEXT, entry_time TEXT, exit_time TEXT, hours_parked REAL, parking_fee REAL, fine_amount REAL, total_paid REAL, payment_method TEXT)"
        };

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}