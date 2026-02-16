package coreParkingSystem;

import EntryModule.Vehicle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO implements GenericDAO<Vehicle, String> {

    @Override
    public void create(Vehicle vehicle) {
        String cleanPlate = vehicle.getLicensePlate().trim().toUpperCase();
        
        // 1. Insert if new (Default fines to 0.0)
        String insertSql = "INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip, accumulated_fines) VALUES(?,?,?,0.0)";
        
        // 2. Update details (Ensures VIP status changes are saved even if vehicle already exists)
        String updateSql = "UPDATE vehicles SET is_vip = ?, type = ? WHERE plate_num = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            
            // Step 1: Try to Insert
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, cleanPlate);
                pstmt.setString(2, vehicle.getVehicleType());
                pstmt.setBoolean(3, vehicle.isVip()); // FIXED: Uses actual VIP status from object
                pstmt.executeUpdate();
            }
            
            // Step 2: Force Update (Matches DB to UI selection)
            try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                pstmt.setBoolean(1, vehicle.isVip());
                pstmt.setString(2, vehicle.getVehicleType());
                pstmt.setString(3, cleanPlate);
                pstmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.out.println("Vehicle Create Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public double getAccumulatedFines(String plateNum) {
        syncTotalFines(plateNum); 
        
        String sql = "SELECT accumulated_fines FROM vehicles WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum.trim().toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("accumulated_fines");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void syncTotalFines(String plateNum) {
        String cleanPlate = plateNum.trim().toUpperCase();
        
        String sumSql = "SELECT SUM(amount) FROM fines WHERE plate_num = ? AND status = 'UNPAID'";
        double totalUnpaid = 0.0;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sumSql)) {
            pstmt.setString(1, cleanPlate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                totalUnpaid = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return; 
        }

        String updateSql = "UPDATE vehicles SET accumulated_fines = ? WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setDouble(1, totalUnpaid);
            pstmt.setString(2, cleanPlate);
            int rows = pstmt.executeUpdate();
            
            if (rows == 0) {
                forceInsertVehicleWithFine(cleanPlate, totalUnpaid);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateAccumulatedFines(String plateNum, double amount) {
        String cleanPlate = plateNum.trim().toUpperCase();
        String updateSql = "UPDATE vehicles SET accumulated_fines = ? WHERE plate_num = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setDouble(1, amount);
            pstmt.setString(2, cleanPlate);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected == 0) {
                forceInsertVehicleWithFine(cleanPlate, amount);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void forceInsertVehicleWithFine(String plateNum, double fineAmount) {
        String insertSql = "INSERT INTO vehicles(plate_num, type, is_vip, accumulated_fines) VALUES(?, 'Unknown', 0, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            
            pstmt.setString(1, plateNum);
            pstmt.setDouble(2, fineAmount);
            pstmt.executeUpdate();
            System.out.println("Vehicle missing in DB. Created record for " + plateNum);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Vehicle read(String plate) { return null; }
    @Override
    public void update(Vehicle vehicle) {}
    @Override
    public void delete(String id) {}
    @Override
    public List<Vehicle> getAll() { return new ArrayList<>(); }
}