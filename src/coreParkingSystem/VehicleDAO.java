package coreParkingSystem;

import EntryModule.Vehicle;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO implements GenericDAO<Vehicle, String> {

    @Override
    public void create(Vehicle vehicle) {
        String sql = "INSERT OR IGNORE INTO vehicles(plate_num, type, is_vip) VALUES(?,?,?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setString(2, vehicle.getVehicleType());
            pstmt.setBoolean(3, false); 
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Vehicle Create Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Vehicle read(String plate) {
        return null; 
    }

    @Override
    public void update(Vehicle vehicle) {}

    @Override
    public void delete(String id) {}

    @Override
    public List<Vehicle> getAll() { return new ArrayList<>(); }
}