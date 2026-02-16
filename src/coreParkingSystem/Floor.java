package coreParkingSystem;
import java.util.*;

// represents one floor in the parking structure and manages its rows
public class Floor {
    private ArrayList<Row> rows = new ArrayList<Row>();
    final int numOfRows = 4;

    public Floor(int floorNum){
        // initialize rows for this floor during creation
        initializeRows(floorNum);
    }

    public int getNumOfRows(){
        return rows.size();
    }

    // creates the fixed number of rows for this floor
    private void initializeRows(int floorNum){
        for(int i = 0; i < numOfRows; i++){
            rows.add(new Row(i+1,floorNum));
        }
    }

    // returns all rows on this floor
    public ArrayList<Row> getRows(){
        return rows;
    }

    // retrieves a specific row by index
    public Row getRow(int index){
        return rows.get(index);
    }
}
