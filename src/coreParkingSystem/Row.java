package coreParkingSystem;
import java.util.*;

public class Row {
    private ArrayList<ParkingSpot> spots = new ArrayList<ParkingSpot>();
    final int numOfSpots = 10;

    public Row(int rNum, int floorNum){
        initializeParkingSpot(floorNum, rNum);
    }
    
    public int getNumOfSpots(){return spots.size();}
    
    private void initializeParkingSpot(int floorNum, int rowNum){
        String idPrefix = floorNum + "-" + rowNum + "-";
        
        for (int i = 0; i < numOfSpots; i++){
            int spotNum = i + 1;
            ParkingSpot.Type type;
            if (i < 3) {
                type = ParkingSpot.Type.COMPACT;
            } 
            else if (i < 6) {
                type = ParkingSpot.Type.REGULAR;
            } 
            else if (i < 8) {
                type = ParkingSpot.Type.RESERVED;
            } 
            else {
                type = ParkingSpot.Type.HANDICAPPED;
            }

            spots.add(new ParkingSpot(idPrefix + spotNum, type, spotNum));
        }
    }
    
    public ArrayList<ParkingSpot> getSpots(){
        return spots;
    }
    
    public ParkingSpot getSpot(int index){
        return spots.get(index);
    }
}