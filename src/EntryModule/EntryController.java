package EntryModule;

import coreParkingSystem.Floor;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.Row;
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
        
        // --- NEW: Get Sequence Number ---
        int seq = lot.getNextSequenceNumber(vehicle.getLicensePlate());

        Ticket ticket = new Ticket.TicketBuilder()
                .addPlate(vehicle.getLicensePlate())
                .addTime(vehicle.getEntryTime())
                .assignSpot(selectedSpotID)
                .addVehicleType(vehicle.getVehicleType())
                .addSpotType(type.toString()) 
                .addSequenceNumber(seq) // Pass to builder
                .build();
        
        vehicle.setTicketId(ticket.toString());
        lot.saveTicket(ticket); 

        return "SUCCESS: " + ticket.toString();
    }

    private boolean isEntryAllowed(Vehicle v, ParkingSpot.Type spotType) {
        if (v instanceof HandicappedVehicle) return true;
        if (v instanceof SUV && spotType == ParkingSpot.Type.COMPACT) return false;
        if (spotType == ParkingSpot.Type.RESERVED) return false; 
        return true; 
    }
}