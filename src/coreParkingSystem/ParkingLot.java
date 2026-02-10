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
    public void addFloorToLot(Floor f){floors.add(f);}
    public Floor getFloor(int index){return floors.get(index);}
    private void initializeFloors(){
        for(int i = 0; i < floorNumber; i++){
            floors.add(new Floor(i));
        }
    }

    public static void main(String[] args){
        System.out.println(ParkingLot.getInstance().getFloor(0));
    }
}
