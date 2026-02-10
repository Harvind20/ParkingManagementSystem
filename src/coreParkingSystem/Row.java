package coreParkingSystem;
import java.util.*;

public class Row {
    private ArrayList<ParkingSpot> spots;
    final int numOfSpots = 10;

    public Row(int rNum, int floorNum){
        initializeParkingSpot(floorNum, rNum);
    }
    public void addSpotToRow(ParkingSpot ps){spots.add(ps);}
    public int getNumOfSpots(){return spots.size();}
    private void initializeParkingSpot(int floorNum, int rowNum){
        String id = floorNum + "-" + rowNum + "-";
        for (int i = 0; i < numOfSpots; i++){
            if (i>=0 && i<=2){
                spots.add(new ParkingSpot(id+i, Global.Type.REGULAR, i));
            }
            else if (i>=3 && i<=5){
                spots.add(new ParkingSpot(id+i, Global.Type.COMPACT, i));
            }
            else if(i>=6 && i<=7){
                spots.add(new ParkingSpot(id+i, Global.Type.HANDICAPPED, i));
            }
            else if(i>=8 && i<=9){
                spots.add(new ParkingSpot(id+i, Global.Type.RESERVED, i));
            }
        }
    }
    private int getSpotIndex(String sID){
        for (int i = 0; i < numOfSpots; i++){
            if(spots.get(i).getSpotID() == sID){
                return i;
            }
        }
        return 0;
    }
    public void changeSpotStatus(String sID, Global.Status status){
        int index = getSpotIndex(sID);
        spots.get(index).setSpotStatus(status);
    }
}
