package coreParkingSystem;

import EntryModule.Ticket;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO implements GenericDAO<Ticket, String> {

    @Override
    public void create(Ticket ticket) {
        String sql = "INSERT INTO tickets(ticket_id, plate_num, spot_id, entry_time) VALUES(?,?,?,?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticket.getTicketID());
            pstmt.setString(2, ticket.getLicensePlate());
            pstmt.setString(3, ticket.getSpotId());
            pstmt.setString(4, ticket.getEntryTime().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Ticket read(String id) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LocalDateTime time = LocalDateTime.parse(rs.getString("entry_time"));
                return new Ticket.TicketBuilder()
                        .addPlate(rs.getString("plate_num"))
                        .assignSpot(rs.getString("spot_id"))
                        .addTime(time)
                        .build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Ticket findByPlate(String plateNum) {
        String sql = "SELECT * FROM tickets WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                LocalDateTime time = LocalDateTime.parse(rs.getString("entry_time"));
                Ticket t = new Ticket.TicketBuilder()
                        .addPlate(rs.getString("plate_num"))
                        .assignSpot(rs.getString("spot_id"))
                        .addTime(time)
                        .build();
                t.setTicketID(rs.getString("ticket_id"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(Ticket ticket) {
    }

    @Override
    public void delete(String plateNum) {
        String sql = "DELETE FROM tickets WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plateNum);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Ticket> getAll() {
        return new ArrayList<>();
    }
}