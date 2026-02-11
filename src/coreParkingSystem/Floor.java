package coreParkingSystem;
import java.util.*;

public class Floor {
    private ArrayList<Row> rows = new ArrayList<Row>();
    final int numOfRows = 4;

    public Floor(int floorNum){
        initializeRows(floorNum);
    }
    public int getNumOfRows(){return rows.size();}
    private void initializeRows(int floorNum){
        for(int i = 0; i < numOfRows; i++){
            rows.add(new Row(i+1,floorNum));
        }
    }
    public ArrayList<Row> getRows(){
        return rows;
    }
    public Row getRow(int index){
        return rows.get(index);
    }
}
