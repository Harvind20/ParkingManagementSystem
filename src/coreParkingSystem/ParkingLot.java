package coreParkingSystem;

import EntryModule.Ticket;
import EntryModule.Vehicle; // Import Vehicle
import java.util.*;

public class ParkingLot {
    private ArrayList<Floor> floors = new ArrayList<>();
    private TicketDAO ticketDAO = new TicketDAO(); 
    private ParkingSpotDAO spotDAO = new ParkingSpotDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO(); // --- NEW: Add DAO ---
    
    // private ReceiptDAO receiptDAO = new ReceiptDAO(); // Keep commented out for now
    
    final int floorNumber = 3;

    private ParkingLot(){
        DatabaseConnection.initializeDB(); 
        initializeFloors();
    }
    
    private static class InstanceHolder{
        private static final ParkingLot INSTANCE = new ParkingLot();
    }
    
    public static ParkingLot getInstance(){
        return InstanceHolder.INSTANCE;
    }
    
    // --- NEW: Save Vehicle Method ---
    public void saveVehicle(Vehicle v) {
        vehicleDAO.create(v);
        System.out.println("[DB] Vehicle saved: " + v.getLicensePlate());
    }
    
    // ... (Keep existing methods: getNextSequenceNumber, saveTicket, etc.) ...

    public int getNextSequenceNumber(String plateNum) {
        return ticketDAO.getTicketCount(plateNum) + 1;
    }
    
    public void saveTicket(Ticket ticket) {
        if (ticket != null) {
            ticketDAO.create(ticket); 
            System.out.println("[DB] Ticket saved for " + ticket.getLicensePlate());
        }
    }

    public Ticket getTicketByPlate(String plate) {
        return ticketDAO.findActiveByPlate(plate);
    }

    public void closeTicket(String ticketId, String plate) {
        ticketDAO.closeTicket(ticketId); 
        System.out.println("[DB] Ticket marked COMPLETED for " + plate);
    }

    public void removeTicket(String plate) {
        Ticket t = getTicketByPlate(plate);
        if(t != null) closeTicket(t.getTicketID(), plate);
    }

    private void initializeFloors(){
        for(int i = 0; i < floorNumber; i++){
            Floor floor = new Floor(i+1);
            floors.add(floor);
            for(Row row : floor.getRows()) {
                for(ParkingSpot spot : row.getSpots()) {
                    spotDAO.create(spot); 
                }
            }
        }
        System.out.println("[System] Parking Spots initialized in Database.");
    }
    
    public ArrayList<Floor> getFloors(){ return floors; }
    
    public void setSpotStatus(String sID, ParkingSpot.Status status){
        ParkingSpot spot = getSpotById(sID);
        if (spot != null) {
            spot.setSpotStatus(status);
            spotDAO.update(spot);
        }
    }

    public ParkingSpot.Status getSpotStatus(String sID){
        ParkingSpot spot = spotDAO.read(sID);
        return (spot != null) ? spot.getSpotStatus() : null;
    }
    
    public ParkingSpot.Type getSpotType(String sID){
        ParkingSpot spot = getSpotById(sID);
        return (spot != null) ? spot.getSpotType() : null;
    }

    private ParkingSpot getSpotById(String sID) {
        String[] idData = sID.split("\\-");
        if(idData.length != 3) return null;
        try {
            int f = Integer.parseInt(idData[0]) - 1;
            int r = Integer.parseInt(idData[1]) - 1;
            int s = Integer.parseInt(idData[2]) - 1;
            if (f >= 0 && f < floors.size()) {
                return floors.get(f).getRow(r).getSpot(s);
            }
        } catch (Exception e) {}
        return null;
    }
}