package coreParkingSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {

    public void createFine(String plateNum, double amount, String reason) {
        String sql = "INSERT INTO fines(plate_num, amount, reason, status) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, reason);
            pstmt.setString(4, "UNPAID");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getUnpaidFinesAmount(String plateNum) {
        String sql = "SELECT SUM(amount) as total FROM fines WHERE plate_num = ? AND status = 'UNPAID'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void markFinesAsPaid(String plateNum) {
        String sql = "UPDATE fines SET status = 'PAID' WHERE plate_num = ? AND status = 'UNPAID'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}