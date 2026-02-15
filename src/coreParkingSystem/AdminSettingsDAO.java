package coreParkingSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import EntryModule.Vehicle;

public class AdminSettingsDAO implements GenericDAO<Object,Integer>{

    public String getCurrentStrategy() {
        String sql = "SELECT setting_value FROM admin_settings WHERE setting_key = 'fine_strategy'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("setting_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "FIXED";
    }

    public void setStrategy(String newStrategy) {
        String sql = "UPDATE admin_settings SET setting_value = ? WHERE setting_key = 'fine_strategy'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStrategy);
            pstmt.executeUpdate();
            System.out.println("[DB] Fine Strategy updated to: " + newStrategy);
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