package coreParkingSystem;
import java.util.*;

public class Row {

    // list of parking spots belonging to this row
    private ArrayList<ParkingSpot> spots = new ArrayList<ParkingSpot>();

    // fixed number of spots per row
    final int numOfSpots = 10;

    public Row(int rNum, int floorNum){
        // create and assign spots when a row is initialized
        initializeParkingSpot(floorNum, rNum);
    }
    
    public int getNumOfSpots(){ return spots.size(); }
    
    private void initializeParkingSpot(int floorNum, int rowNum){

        // prefix used to generate unique spot IDs Floor-Row-Spot
        String idPrefix = floorNum + "-" + rowNum + "-";
        
        for (int i = 0; i < numOfSpots; i++){
            int spotNum = i + 1;

            // assign spot types based on position within the row
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

            // create spot and add to this row
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
