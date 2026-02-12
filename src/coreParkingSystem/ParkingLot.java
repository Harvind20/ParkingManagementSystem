package coreParkingSystem;

import EntryModule.Ticket;
import java.util.*;

public class ParkingLot {
    private ArrayList<Floor> floors = new ArrayList<>();
    
    // DAOs
    private TicketDAO ticketDAO = new TicketDAO(); 
    private ParkingSpotDAO spotDAO = new ParkingSpotDAO();
    private ReceiptDAO receiptDAO = new ReceiptDAO(); // Added ReceiptDAO
    
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
    
    // --- SEQUENCE GENERATION ---
    public int getNextSequenceNumber(String plateNum) {
        // Count active tickets + past receipts + 1
        int active = ticketDAO.getTicketCount(plateNum);
        int past = receiptDAO.getReceiptCount(plateNum);
        return active + past + 1;
    }
    
    // --- DATABASE INTEGRATION METHODS ---
    public void saveTicket(Ticket ticket) {
        if (ticket != null) {
            ticketDAO.create(ticket); 
            System.out.println("[DB] Ticket saved for " + ticket.getLicensePlate());
        }
    }

    public Ticket getTicketByPlate(String plate) {
        return ticketDAO.findByPlate(plate); 
    }

    public void removeTicket(String plate) {
        ticketDAO.delete(plate); 
        System.out.println("[DB] Ticket closed for " + plate);
    }

    // --- EXISTING FLOOR LOGIC ---
    private void initializeFloors(){
        for(int i = 0; i < floorNumber; i++){
            floors.add(new Floor(i+1));
        }
    }
    
    public ArrayList<Floor> getFloors(){ return floors; }
    
    public Floor getFloor(int index){
        if (index < 0 || index >= floors.size()) return null;
        return floors.get(index);
    }

    public void setSpotStatus(String sID, ParkingSpot.Status status){
        ParkingSpot spot = getSpotById(sID);
        if (spot != null) {
            spot.setSpotStatus(status);
            spotDAO.create(spot); 
            spotDAO.update(spot);
        }
    }

    public ParkingSpot.Status getSpotStatus(String sID){
        ParkingSpot spot = getSpotById(sID);
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
            if (f >= 0 && r >= 0 && s >= 0)
                return floors.get(f).getRow(r).getSpot(s);
        } catch (Exception e) {}
        return null;
    }
}