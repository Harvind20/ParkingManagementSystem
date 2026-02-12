package coreParkingSystem;

import ExitModule.Receipt;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceiptDAO implements GenericDAO<Receipt, String> {

    @Override
    public void create(Receipt receipt) {
        String sql = "INSERT INTO receipts(receipt_id, ticket_id, plate_num, spot_id, entry_time, exit_time, hours_parked, parking_fee, fine_amount, total_paid, payment_method) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, UUID.randomUUID().toString());
            pstmt.setString(2, "TIX-" + receipt.getLicensePlate()); 
            pstmt.setString(3, receipt.getLicensePlate());
            pstmt.setString(4, "UNKNOWN"); 
            pstmt.setString(5, LocalDateTime.now().toString()); 
            pstmt.setString(6, LocalDateTime.now().toString());
            pstmt.setDouble(7, 0.0);
            pstmt.setDouble(8, receipt.getParkingFee());
            pstmt.setDouble(9, receipt.getFines());
            pstmt.setDouble(10, receipt.getAmountPaid());
            pstmt.setString(11, "CASH");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Receipt read(String id) {
        return null;
    }

    @Override
    public void update(Receipt t) {
    }

    @Override
    public void delete(String id) {
    }

    @Override
    public List<Receipt> getAll() {
        return new ArrayList<>();
    }
}