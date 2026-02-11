package coreParkingSystem;
import java.util.*;

public class ParkingLot {
    private ArrayList<Floor> floors = new ArrayList<Floor>();
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
    private void initializeFloors(){
        for(int i = 0; i < floorNumber; i++){
            floors.add(new Floor(i+1));
        }
    }
    public ArrayList<Floor> getFloors(){
        return floors;
    }
    public Floor getFloor(int index){
        return floors.get(index);
    }
    public void setSpotStatus(String sID, ParkingSpot.Status status){
        String idData[] = sID.split("\\-");
        if((idData.length) != 3){return;}
        else{
            int floorNum = Integer.parseInt(idData[0]) - 1;
            int rowNum = Integer.parseInt(idData[1]) - 1;
            int spotNum = Integer.parseInt(idData[2]) - 1;
            if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                return;
            }
            ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).setSpotStatus(status);
        }
    }
    public ParkingSpot.Status getSpotStatus(String sID){
        String idData[] = sID.split("\\-");
        ParkingSpot.Status status = null;
        if((idData.length) != 3){return status;}
        else{
            int floorNum = Integer.parseInt(idData[0]) - 1;
            int rowNum = Integer.parseInt(idData[1]) - 1;
            int spotNum = Integer.parseInt(idData[2]) - 1;
            if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                return status;
            }
            status = ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).getSpotStatus();
        }
        return status;
    }
    public void setSpotType(String sID, ParkingSpot.Type type){
        String idData[] = sID.split("\\-");
        if((idData.length) != 3){return;}
        else{
            int floorNum = Integer.parseInt(idData[0]) - 1;
            int rowNum = Integer.parseInt(idData[1]) - 1;
            int spotNum = Integer.parseInt(idData[2]) - 1;
            if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                return;
            }
            ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).setSpotType(type);
        }
    }
    public ParkingSpot.Type getSpotType(String sID){
        String idData[] = sID.split("\\-");
        ParkingSpot.Type type = null;
        if((idData.length) != 3){return type;}
        else{
            int floorNum = Integer.parseInt(idData[0])-1;
            int rowNum = Integer.parseInt(idData[1])-1;
            int spotNum = Integer.parseInt(idData[2])-1;
            if (floorNum < 0 || rowNum < 0 || spotNum < 0){
                return type;
            }
            type = ParkingLot.getInstance().getFloor(floorNum).getRow(rowNum).getSpot(spotNum).getSpotType();
        }
        return type;
    }

    public static void main(String[] args){}
}
