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
            floors.add(new Floor(i));
        }
    }
    public ArrayList<Floor> getFloors(){
        return floors;
    }

    public static void main(String[] args){
        
    }
}
