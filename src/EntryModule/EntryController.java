package EntryModule;

import FineModule.FineManager;
import FineModule.FineScheme;
import UserInterface.ParkingTicketUI;
import coreParkingSystem.FineDAO;
import coreParkingSystem.Floor;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import coreParkingSystem.Row;
import coreParkingSystem.VehicleDAO;
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

             return "ERROR: Vehicle type not allowed in this spot";
        }

        lot.saveVehicle(vehicle);

        String currentSchemeName = "FIXED";
        if (FineManager.getCurrentScheme() instanceof FineModule.HourlyFine) {
            currentSchemeName = "HOURLY";
        } else if (FineManager.getCurrentScheme() instanceof FineModule.ProgressiveFine) {
            currentSchemeName = "PROGRESSIVE";
        }

        Ticket ticket = new Ticket.TicketBuilder()
            .addPlate(vehicle.getLicensePlate())
            .addTime(vehicle.getEntryTime())
            .addVehicleType(vehicle.getVehicleType())
            .addSequenceNumber(lot.getNextSequenceNumber(vehicle.getLicensePlate()))
            .addFineScheme(currentSchemeName)
            .build();
        
        vehicle.setTicketId(ticket.toString());
        lot.saveTicket(ticket);

        lot.setSpotStatus(selectedSpotID, ParkingSpot.Status.OCCUPIED);
        ParkingSpot spot = lot.getSpotById(selectedSpotID);
        spot.setCurrentlyParkedVehicleID(vehicle.getLicensePlate());
        lot.updateSpotOccupancy(spot);

        checkAndIssueViolationFine(vehicle, spot, currentSchemeName);

        vehicleDAO.syncTotalFines(vehicle.getLicensePlate());

        SwingUtilities.invokeLater(() -> 
            new ParkingTicketUI("SUCCESS: " + ticket.toString(), vehicle, vehicle.isVip(), selectedSpotID).setVisible(true)
        );

        return "SUCCESS";
    }

    private boolean isEntryAllowed(Vehicle v, ParkingSpot.Type spotType) {
        if (v instanceof HandicappedVehicle) return true;
        // Strict Block: SUV in Compact
        if (v instanceof SUV && spotType == ParkingSpot.Type.COMPACT) return false;
        return true; 
    }

    private void checkAndIssueViolationFine(Vehicle vehicle, ParkingSpot spot, String schemeName) {
        boolean isViolation = false;
        String reason = "";

        if (spot.getSpotType() == ParkingSpot.Type.RESERVED && !vehicle.isVip()) {
            isViolation = true;
            reason = "Violation: Non-VIP in Reserved Spot";
        } 
        else if (spot.getSpotType() == ParkingSpot.Type.HANDICAPPED && !(vehicle instanceof HandicappedVehicle)) {
            isViolation = true;
            reason = "Violation: Unauthorized in Handicap Spot";
        }

        if (isViolation) {
            FineManager fm = new FineManager();
            FineScheme scheme = FineManager.getSchemeByName(schemeName);
            double initialFine = fm.calculateFine(scheme, 1, true); 
            
            if (initialFine > 0) {
                FineDAO fineDAO = new FineDAO();
                fineDAO.createFine(vehicle.getLicensePlate(), initialFine, reason);
                System.out.println("Immediate Violation Fine Issued: RM " + initialFine);
            }
        }
    }
}