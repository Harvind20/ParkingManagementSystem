package coreParkingSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpotDAO implements GenericDAO<ParkingSpot, String> {

    @Override
    public void create(ParkingSpot spot) {
        String sql = "INSERT OR IGNORE INTO parking_spots(spot_id, type, status) VALUES(?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spot.getSpotID());
            pstmt.setString(2, spot.getSpotType().toString());
            pstmt.setString(3, spot.getSpotStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ParkingSpot read(String id) {
        String sql = "SELECT * FROM parking_spots WHERE spot_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                ParkingSpot.Type type = ParkingSpot.Type.valueOf(rs.getString("type"));
                int spotNum = Integer.parseInt(id.split("-")[2]);
                ParkingSpot spot = new ParkingSpot(rs.getString("spot_id"), type, spotNum);
                spot.setSpotStatus(ParkingSpot.Status.valueOf(rs.getString("status")));
                return spot;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(ParkingSpot spot) {
        String sql = "UPDATE parking_spots SET status = ? WHERE spot_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spot.getSpotStatus().toString());
            pstmt.setString(2, spot.getSpotID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM parking_spots WHERE spot_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ParkingSpot> getAll() {
        List<ParkingSpot> list = new ArrayList<>();
        String sql = "SELECT * FROM parking_spots";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ParkingSpot.Type type = ParkingSpot.Type.valueOf(rs.getString("type"));
                int spotNum = Integer.parseInt(rs.getString("spot_id").split("-")[2]);
                ParkingSpot spot = new ParkingSpot(rs.getString("spot_id"), type, spotNum);
                spot.setSpotStatus(ParkingSpot.Status.valueOf(rs.getString("status")));
                list.add(spot);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}