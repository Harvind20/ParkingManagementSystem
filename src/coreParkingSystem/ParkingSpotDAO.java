package coreParkingSystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// DAO responsible for persisting and retrieving parking spot data
public class ParkingSpotDAO implements GenericDAO<ParkingSpot, String> {

    @Override
    public void create(ParkingSpot spot) {
        // inserts spot record only if it does not already exist
        String sql = "INSERT OR IGNORE INTO parking_spots(spot_id, type, status,plate_num) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, spot.getSpotID());
            pstmt.setString(2, spot.getSpotType().toString());
            pstmt.setString(3, spot.getSpotStatus().toString());
            pstmt.setString(4, spot.getCurrentlyParkedVehicleID());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // finds a spot based on the vehicle currently occupying it
    public ParkingSpot findByPlate(String plateNum){
        String sql = "SELECT * FROM parking_spots WHERE plate_num = ?";
        try(Connection conn = DatabaseConnection.connect();
            PreparedStatement p = conn.prepareStatement(sql)) {

            p.setString(1, plateNum);
            ResultSet rs = p.executeQuery();

            if (rs.next()){
                ParkingSpot spot = new ParkingSpot(
                    rs.getString("spot_id"),
                    ParkingSpot.Type.valueOf(rs.getString("type")),
                    Integer.parseInt(rs.getString("spot_id").split("-")[2])
                );
                return spot;
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ParkingSpot read(String id) {
        // retrieves a single spot using its ID
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
        // updates occupancy status only 
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

    // updates which vehicle is currently occupying the spot
    public void updateCurrentlyParkedVehicle(ParkingSpot spot){
        String sql = "UPDATE parking_spots SET plate_num = ? WHERE spot_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, spot.getCurrentlyParkedVehicleID());
            pstmt.setString(2, spot.getSpotID());
            pstmt.executeUpdate();

        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        // removes spot record from database
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
        // loads all spots and reconstructs objects from DB rows
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
