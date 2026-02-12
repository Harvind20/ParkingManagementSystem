package coreParkingSystem;
import EntryModule.Ticket;
import java.util.*;

public class ParkingLot {
    private ArrayList<Floor> floors = new ArrayList<Floor>();
    private Map<String, Ticket> activeTickets = new HashMap<>(); 
    
    final int floorNumber = 3;

    private ParkingLot(){
        initializeFloors();
    }
    
    private static class InstanceHolder{
        private static final ParkingLot INSTANCE = new ParkingLot();
    }
    
    public static ParkingLot getInstance(){
        return InstanceHolder.INSTANCE;
    }
    
    public void saveTicket(Ticket ticket) {
        if (ticket != null) {
            activeTickets.put(ticket.getLicensePlate(), ticket);
            System.out.println("[DB] Ticket saved for " + ticket.getLicensePlate());
        }
    }

    public Ticket getTicketByPlate(String plate) {
        return activeTickets.get(plate);
    }

    public void removeTicket(String plate) {
        activeTickets.remove(plate);
        System.out.println("[DB] Ticket closed for " + plate);
    }

    private void initializeFloors(){
        for(int i = 0; i < floorNumber; i++){
            floors.add(new Floor(i+1));
        }
    }
    
    public ArrayList<Floor> getFloors(){
        return floors;
    }
    
    public Floor getFloor(int index){
        if (index < 0 || index >= floors.size()) return null;
        return floors.get(index);
    }
    
    public void setSpotStatus(String sID, ParkingSpot.Status status){
        String idData[] = sID.split("\\-");
        if((idData.length) != 3){return;}
        else{
            try {
                int floorNum = Integer.parseInt(idData[0]) - 1;
                int rowNum = Integer.parseInt(idData[1]) - 1;
                int spotNum = Integer.parseInt(idData[2]) - 1;
                if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                    return;
                }
                ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).setSpotStatus(status);
            } catch (Exception e) {
                System.out.println("Error setting status: " + e.getMessage());
            }
        }
    }
    
    public ParkingSpot.Status getSpotStatus(String sID){
        String idData[] = sID.split("\\-");
        ParkingSpot.Status status = null;
        if((idData.length) != 3){return status;}
        else{
            try {
                int floorNum = Integer.parseInt(idData[0]) - 1;
                int rowNum = Integer.parseInt(idData[1]) - 1;
                int spotNum = Integer.parseInt(idData[2]) - 1;
                if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                    return status;
                }
                status = ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).getSpotStatus();
            } catch (Exception e) {
                return null;
            }
        }
        return status;
    }
    
    public void setSpotType(String sID, ParkingSpot.Type type){
        String idData[] = sID.split("\\-");
        if((idData.length) != 3){return;}
        else{
            try {
                int floorNum = Integer.parseInt(idData[0]) - 1;
                int rowNum = Integer.parseInt(idData[1]) - 1;
                int spotNum = Integer.parseInt(idData[2]) - 1;
                if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                    return;
                }
                ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).setSpotType(type);
            } catch (Exception e) {}
        }
    }
    
    public ParkingSpot.Type getSpotType(String sID){
        String idData[] = sID.split("\\-");
        ParkingSpot.Type type = null;
        if((idData.length) != 3){return type;}
        else{
            try {
                int floorNum = Integer.parseInt(idData[0])-1;
                int rowNum = Integer.parseInt(idData[1])-1;
                int spotNum = Integer.parseInt(idData[2])-1;
                if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                    return type;
                }
                type = ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).getSpotType();
            } catch (Exception e) {
                return null;
            }
        }
        return type;
    }

    public static void main(String[] args){}
}