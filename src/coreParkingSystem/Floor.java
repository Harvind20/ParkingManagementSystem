package coreParkingSystem;
import java.util.*;

public class Floor {
    private int floorNumber;
    private ArrayList<Row> rows;
    final int numOfRows = 4;

    public int getFloorNumber(){return floorNumber;}
    public void setFloorNumber(int fn){floorNumber = fn;}
    public void addRowToFloor(Row r){rows.add(r);}
}
