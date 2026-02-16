package coreParkingSystem;

import EntryModule.Ticket;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// DAO responsible for storing and retrieving ticket records from the database
public class TicketDAO implements GenericDAO<Ticket, String> {

    // format used to store entry time consistently in the database
    private static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");

    @Override
    public void create(Ticket ticket) {
        String sql = "INSERT INTO tickets(ticket_id, plate_num, entry_time, status, fine_scheme) VALUES(?,?,?,?,?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // save core ticket details including fine scheme active at entry time
            pstmt.setString(1, ticket.getTicketID());
            pstmt.setString(2, ticket.getLicensePlate());
            pstmt.setString(3, ticket.getEntryTime().format(DB_FORMATTER));
            pstmt.setString(4, "ACTIVE");
            
            String scheme = ticket.getFineSchemeAtEntry();
            if (scheme == null) scheme = "FIXED";
            pstmt.setString(5, scheme);
            
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Ticket Create Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // retrieves the active ticket for a given vehicle plate
    public Ticket findActiveByPlate(String plateNum) {
        String sql = "SELECT * FROM tickets WHERE plate_num = ? AND status = 'ACTIVE'";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNum);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // reconstruct entry time and scheme from DB
                LocalDateTime time = LocalDateTime.parse(rs.getString("entry_time"), DB_FORMATTER);
                
                String scheme = rs.getString("fine_scheme");
                if (scheme == null) scheme = "FIXED";

                // rebuild ticket object using builder
                Ticket t = new Ticket.TicketBuilder()
                        .addPlate(rs.getString("plate_num"))
                        .addTime(time)
                        .addFineScheme(scheme)
                        .build();

                t.setTicketID(rs.getString("ticket_id"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // marks a ticket as completed once exit process is finished
    public void closeTicket(String ticketId) {
        String sql = "UPDATE tickets SET status = 'COMPLETED' WHERE ticket_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticketId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // used to generate sequence numbers for repeat visits by same vehicle
    public int getTicketCount(String plateNum) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNum);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    // unused interface methods
    @Override public Ticket read(String id) { return null; }
    @Override public void update(Ticket ticket) {}
    @Override public void delete(String id) {} 
    @Override public List<Ticket> getAll() { return new ArrayList<>(); }
}
