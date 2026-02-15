package coreParkingSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FineDAO implements GenericDAO<Object, Integer> {

    public double getUnpaidFinesAmount(String plateNum) {
        String sql = "SELECT SUM(amount) FROM fines WHERE plate_num = ? AND status = 'UNPAID'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, plateNum.trim().toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
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
            
            pstmt.setString(1, plateNum.trim().toUpperCase());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createFine(String plateNum, double amount, String reason) {
        String sql = "INSERT INTO fines(plate_num, amount, reason, status, date_issued) VALUES(?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pstmt.setString(1, plateNum.trim().toUpperCase());
            pstmt.setDouble(2, amount);
            pstmt.setString(3, reason);
            pstmt.setString(4, "UNPAID");
            pstmt.setString(5, timestamp);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override public void create(Object t) {}
    @Override public Object read(Integer id) { return null; }
    @Override public void update(Object t) {}
    @Override public void delete(Integer id) {}
    @Override public List<Object> getAll() { return new ArrayList<>(); }
}