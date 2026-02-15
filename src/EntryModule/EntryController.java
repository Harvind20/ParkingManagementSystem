package EntryModule;

import coreParkingSystem.Floor;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.Row;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;

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

        lot.setSpotStatus(selectedSpotID, ParkingSpot.Status.OCCUPIED);
        ParkingSpot spot = lot.getSpotById(selectedSpotID);
        spot.setCurrentlyParkedVehicleID(vehicle.getLicensePlate());
        lot.updateSpotOccupancy(spot);
        if (type == ParkingSpot.Type.RESERVED && !vehicle.isVip()) {
            createReservedFine(vehicle.getLicensePlate());
        }
        return "SUCCESS";
    }

    private boolean isEntryAllowed(Vehicle v, ParkingSpot.Type spotType) {
        if (v instanceof HandicappedVehicle) return true;
        if (v instanceof SUV && spotType == ParkingSpot.Type.COMPACT) return false;
        return true; 
    }

    private void createReservedFine(String plate) {
        try {
            Connection conn = coreParkingSystem.DatabaseConnection.connect();

            String sql = "INSERT INTO fines (plate_num, amount, reason, status) VALUES (?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, plate);
            pst.setDouble(2, 50.0);
            pst.setString(3, "Non-VIP parked in RESERVED spot");
            pst.setString(4, "UNPAID");

            pst.executeUpdate();

            pst.close();
            conn.close();

            System.out.println("Fine created for " + plate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}