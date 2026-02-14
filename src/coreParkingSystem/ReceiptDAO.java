package coreParkingSystem;

import ExitModule.Receipt;
import java.sql.*;
import java.time.LocalDateTime;
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

    public Receipt getLatestReceipt(String plateNum) {
        String sql = "SELECT r.*, v.type AS v_type, s.type AS s_type " +
                     "FROM receipts r " +
                     "LEFT JOIN vehicles v ON r.plate_num = v.plate_num " +
                     "LEFT JOIN parking_spots s ON r.spot_id = s.spot_id " +
                     "WHERE r.plate_num = ? " +
                     "ORDER BY r.exit_time DESC " +
                     "LIMIT 1";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, plateNum);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Parse Times
                LocalDateTime entryTime = LocalDateTime.parse(rs.getString("entry_time"));
                LocalDateTime exitTime = LocalDateTime.parse(rs.getString("exit_time"));

                String vType = rs.getString("v_type");
                if(vType == null) vType = "Vehicle";
                
                String sType = rs.getString("s_type");
                if(sType == null) sType = "Parking Spot";

                // Reconstruct Receipt Object
                return new Receipt(
                    rs.getString("plate_num"),
                    entryTime,
                    exitTime,
                    rs.getString("spot_id"),
                    sType,
                    vType,
                    rs.getDouble("hours_parked"),
                    rs.getDouble("parking_fee"),
                    rs.getDouble("fine_amount"),
                    0.0,
                    rs.getDouble("parking_fee"),
                    rs.getDouble("fine_amount"),
                    rs.getDouble("total_paid"),
                    0.0,
                    rs.getString("payment_method"),
                    rs.getString("ticket_id"),
                    true
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override public Receipt read(String id) { return null; }
    @Override public void update(Receipt t) {}
    @Override public void delete(String id) {}
    @Override public List<Receipt> getAll() { return new ArrayList<>(); }
}