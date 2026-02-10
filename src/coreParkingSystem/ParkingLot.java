package coreParkingSystem;
import java.util.*;

public class ParkingLot {//3 floors
    private ArrayList<Floor> floors;

    private ParkingLot(){}
    private static class InstanceHolder{
        private static final ParkingLot INSTANCE = new ParkingLot();
    }
    public static ParkingLot getInstance(){
        return InstanceHolder.INSTANCE;
    }
}
