package coreParkingSystem;

import EntryModule.Vehicle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// DAO that manages vehicle records and their accumulated fines
public class VehicleDAO implements GenericDAO<Vehicle, String> {

    @Override
    public void create(Vehicle vehicle) {

        // normalize plate format for consistency in database
        String cleanPlate = vehicle.getLicensePlate().trim().toUpperCase();
        
        // insert new vehicle if it doesn't exist
        String insertSql = "INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip, accumulated_fines) VALUES(?,?,?,0.0)";
        
        // always update details to reflect latest VIP/type status from UI
        String updateSql = "UPDATE vehicles SET is_vip = ?, type = ? WHERE plate_num = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            
            // attempt to insert record first
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                pstmt.setString(1, cleanPlate);
                pstmt.setString(2, vehicle.getVehicleType());
                pstmt.setBoolean(3, vehicle.isVip());
                pstmt.executeUpdate();
            }
            
            // force update to ensure latest settings are saved
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

    // retrieves the current unpaid fine total for a vehicle
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

    // recalculates unpaid fines from fines table and updates vehicle record
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

        // update accumulated fines field in vehicles table
        String updateSql = "UPDATE vehicles SET accumulated_fines = ? WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setDouble(1, totalUnpaid);
            pstmt.setString(2, cleanPlate);
            int rows = pstmt.executeUpdate();
            
            // if vehicle does not exist yet, create placeholder record
            if (rows == 0) {
                forceInsertVehicleWithFine(cleanPlate, totalUnpaid);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // manually sets accumulated fines value
    public void updateAccumulatedFines(String plateNum, double amount) {

        String cleanPlate = plateNum.trim().toUpperCase();
        String updateSql = "UPDATE vehicles SET accumulated_fines = ? WHERE plate_num = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
            
            pstmt.setDouble(1, amount);
            pstmt.setString(2, cleanPlate);
            int rowsAffected = pstmt.executeUpdate();

            // create record if vehicle is missing
            if (rowsAffected == 0) {
                forceInsertVehicleWithFine(cleanPlate, amount);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // fallback method to ensure a vehicle record exists in DB
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

    // unused interface methods
    @Override public Vehicle read(String plate) { return null; }
    @Override public void update(Vehicle vehicle) {}
    @Override public void delete(String id) {}
    @Override public List<Vehicle> getAll() { return new ArrayList<>(); }
}
