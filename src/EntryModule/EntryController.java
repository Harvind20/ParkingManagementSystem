package EntryModule;

import UserInterface.ParkingTicketUI;
import coreParkingSystem.DatabaseConnection;
import coreParkingSystem.Floor;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.Row;
import coreParkingSystem.VehicleDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.SwingUtilities;

public class EntryController {

    public boolean checkSystemFull() {
        ParkingLot lot = ParkingLot.getInstance();
        ArrayList<Floor> floors = lot.getFloors();
        
        for (Floor f : floors) {
            for (Row r : f.getRows()) {
                for (ParkingSpot s : r.getSpots()) {
                    if (s.getSpotStatus() == ParkingSpot.Status.AVAILABLE) {
                        return false; 
                    }
                }
            }
        }
        return true;
    }

    public String attemptPark(Vehicle vehicle, String selectedSpotID) {
        ParkingLot lot = ParkingLot.getInstance();
        VehicleDAO vehicleDAO = new VehicleDAO(); 

        ParkingSpot.Status status = lot.getSpotStatus(selectedSpotID);
        ParkingSpot.Type type = lot.getSpotType(selectedSpotID);

        if (status == null || type == null) {
            return "ERROR: Invalid Spot ID format (Use 'Floor-Row-Spot', e.g., '1-1-1')";
        }

        if (status == ParkingSpot.Status.OCCUPIED) {
            return "ERROR: Spot Occupied";
        }

        if (!isEntryAllowed(vehicle, type)) {
             if (vehicle instanceof SUV && type == ParkingSpot.Type.COMPACT) 
                 return "ERROR: SUV cannot park in Compact spot";
             if (type == ParkingSpot.Type.RESERVED) 
                 return "ERROR: This spot is Reserved for VIPs";
             
             return "ERROR: Vehicle type not allowed in this spot";
        }

        lot.saveVehicle(vehicle);

        Ticket ticket = new Ticket.TicketBuilder()
            .addPlate(vehicle.getLicensePlate())
            .addTime(vehicle.getEntryTime())
            .addVehicleType(vehicle.getVehicleType())
            .addSequenceNumber(lot.getNextSequenceNumber(vehicle.getLicensePlate()))
            .build();
        
        vehicle.setTicketId(ticket.toString());

        lot.saveTicket(ticket);

        lot.setSpotStatus(selectedSpotID, ParkingSpot.Status.OCCUPIED);
        ParkingSpot spot = lot.getSpotById(selectedSpotID);
        spot.setCurrentlyParkedVehicleID(vehicle.getLicensePlate());
        lot.updateSpotOccupancy(spot);

        // Fine if non-handicap parked in handicap spot
        if (type == ParkingSpot.Type.HANDICAPPED && !(vehicle instanceof HandicappedVehicle)) {
            createHandicapViolationFine(vehicle.getLicensePlate());
        }

        if (type == ParkingSpot.Type.RESERVED && !vehicle.isVip()) {
            createReservedFine(vehicle.getLicensePlate());
        }
        
        vehicleDAO.syncTotalFines(vehicle.getLicensePlate());

        SwingUtilities.invokeLater(() -> 
            new ParkingTicketUI("SUCCESS: " + ticket.toString(), vehicle, vehicle.isVip(), selectedSpotID).setVisible(true)
        );

        return "SUCCESS";
    }

    private boolean isEntryAllowed(Vehicle v, ParkingSpot.Type spotType) {
        if (v instanceof HandicappedVehicle) return true;
        if (v instanceof SUV && spotType == ParkingSpot.Type.COMPACT) return false;
        return true; 
    }

    private void createReservedFine(String plate) {
        try {
            Connection conn = DatabaseConnection.connect();

            String sql = "INSERT INTO fines (plate_num, amount, reason, status, date_issued) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pst.setString(1, plate);
            pst.setDouble(2, 50.0);
            pst.setString(3, "Non-VIP parked in RESERVED spot");
            pst.setString(4, "UNPAID");
            pst.setString(5, timestamp);

            pst.executeUpdate();

            pst.close();
            conn.close();

            System.out.println("Fine created for " + plate);
            new VehicleDAO().syncTotalFines(plate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createHandicapViolationFine(String plate) {
        try {
            Connection conn = DatabaseConnection.connect();

            String sql = "INSERT INTO fines (plate_num, amount, reason, status, date_issued) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pst.setString(1, plate);
            pst.setDouble(2, 100.0);
            pst.setString(3, "Non-handicapped vehicle parked in HANDICAP spot");
            pst.setString(4, "UNPAID");
            pst.setString(5, timestamp);

            pst.executeUpdate();

            pst.close();
            conn.close();

            System.out.println("Handicap violation fine issued.");
            new VehicleDAO().syncTotalFines(plate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}