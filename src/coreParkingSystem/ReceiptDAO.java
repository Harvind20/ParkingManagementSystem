package coreParkingSystem;

import ExitModule.Receipt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReceiptDAO implements GenericDAO<Receipt, String> {

    @Override
    public void create(Receipt receipt) {
        String sql = "INSERT INTO receipts(receipt_id, ticket_id, plate_num, spot_id, entry_time, exit_time, hours_parked, parking_fee, fine_amount, total_paid, payment_method) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, receipt.getReceiptNumber());
            pstmt.setString(2, receipt.getTicketID()); 
            pstmt.setString(3, receipt.getLicensePlate());
            pstmt.setString(4, receipt.getSpotId()); 
            pstmt.setString(5, receipt.getEntryTime().toString()); 
            pstmt.setString(6, receipt.getExitTime().toString());
            pstmt.setDouble(7, receipt.getHoursParked());
            pstmt.setDouble(8, receipt.getParkingFee());
            pstmt.setDouble(9, receipt.getFines());
            pstmt.setDouble(10, receipt.getAmountPaid());
            pstmt.setString(11, receipt.getPaymentMethod());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getReceiptCount(String plateNum) {
        String sql = "SELECT COUNT(*) FROM receipts WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Receipt read(String id) { return null; }

    @Override
    public void update(Receipt t) {}

    @Override
    public void delete(String id) {}

    @Override
    public List<Receipt> getAll() { return new ArrayList<>(); }
}