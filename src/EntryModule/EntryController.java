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

    // checks if every parking spot in the system is occupied
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

    // main entry logic which validates spot, creates ticket, updates DB, and shows ticket UI
    public String attemptPark(Vehicle vehicle, String selectedSpotID) {
        ParkingLot lot = ParkingLot.getInstance();
        VehicleDAO vehicleDAO = new VehicleDAO(); 

        ParkingSpot.Status status = lot.getSpotStatus(selectedSpotID);
        ParkingSpot.Type type = lot.getSpotType(selectedSpotID);

        // ensure the provided spot id exists and is valid
        if (status == null || type == null) {
            return "ERROR: Invalid Spot ID format (Use 'Floor-Row-Spot', e.g., '1-1-1')";
        }

        // prevent parking if spot is already occupied
        if (status == ParkingSpot.Status.OCCUPIED) {
            return "ERROR: Spot Occupied";
        }

        // enforce vehicle to spot compatibility rules
        if (!isEntryAllowed(vehicle, type)) {
             if (vehicle instanceof SUV && type == ParkingSpot.Type.COMPACT) 
                 return "ERROR: SUV cannot park in Compact spot";

             return "ERROR: Vehicle type not allowed in this spot";
        }

        // persist vehicle record before generating ticket
        lot.saveVehicle(vehicle);

        // determine which fine scheme is currently active
        String currentSchemeName = "FIXED";
        if (FineManager.getCurrentScheme() instanceof FineModule.HourlyFine) {
            currentSchemeName = "HOURLY";
        } else if (FineManager.getCurrentScheme() instanceof FineModule.ProgressiveFine) {
            currentSchemeName = "PROGRESSIVE";
        }

        // build ticket using builder pattern to keep creation structured and flexible
        Ticket ticket = new Ticket.TicketBuilder()
            .addPlate(vehicle.getLicensePlate())
            .addTime(vehicle.getEntryTime())
            .addVehicleType(vehicle.getVehicleType())
            .addSequenceNumber(lot.getNextSequenceNumber(vehicle.getLicensePlate()))
            .addFineScheme(currentSchemeName)
            .build();
        
        // link ticket to vehicle and store ticket in system
        vehicle.setTicketId(ticket.toString());
        lot.saveTicket(ticket);

        // mark spot as occupied and associate it with this vehicle
        lot.setSpotStatus(selectedSpotID, ParkingSpot.Status.OCCUPIED);
        ParkingSpot spot = lot.getSpotById(selectedSpotID);
        spot.setCurrentlyParkedVehicleID(vehicle.getLicensePlate());
        lot.updateSpotOccupancy(spot);

        // immediately check for rule violations and issue fine if needed
        checkAndIssueViolationFine(vehicle, spot, currentSchemeName);

        // sync fine totals so future exit calculations remain accurate
        vehicleDAO.syncTotalFines(vehicle.getLicensePlate());

        // open ticket UI on the Swing thread
        SwingUtilities.invokeLater(() -> 
            new ParkingTicketUI("SUCCESS: " + ticket.toString(), vehicle, vehicle.isVip(), selectedSpotID).setVisible(true)
        );

        return "SUCCESS";
    }

    // determines whether the selected vehicle is allowed to park in the given spot type
    private boolean isEntryAllowed(Vehicle v, ParkingSpot.Type spotType) {
        if (v instanceof HandicappedVehicle) return true;

        // strict rule where SUVs cannot use compact spots
        if (v instanceof SUV && spotType == ParkingSpot.Type.COMPACT) return false;

        return true; 
    }

    // checks for reserved and handicapped violations and applies an immediate fine if required
    private void checkAndIssueViolationFine(Vehicle vehicle, ParkingSpot spot, String schemeName) {
        boolean isViolation = false;
        String reason = "";

        boolean isHandicapped = (vehicle instanceof HandicappedVehicle) || 
                                vehicle.getVehicleType().equalsIgnoreCase("Handicapped") || 
                                vehicle.getVehicleType().equalsIgnoreCase("HandicappedVehicle");

        // FIX: Check if it is Handicapped OR VIP before issuing Reserved spot fine
        if (spot.getSpotType() == ParkingSpot.Type.RESERVED) {
            if (!vehicle.isVip() && !isHandicapped) {
                isViolation = true;
                reason = "Violation: Non-VIP in Reserved Spot";
            }
        }
        // non-handicapped driver using handicapped spot
        else if (spot.getSpotType() == ParkingSpot.Type.HANDICAPPED && !isHandicapped) {
            isViolation = true;
            reason = "Violation: Unauthorized in Handicap Spot";
        }

        // create initial fine entry immediately if a violation is detected
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