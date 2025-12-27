public class ParkingLot{
    private String spotID;
    private int floorNumber;
    private int rowNumber;
    private int spotNumber;
    private int spotType;
    private int spotStatus;

    public String getSpotID(){return spotID;}
    public void setSpotID(String i){spotID = i;}
    public int getFloorNumber(){return floorNumber;}
    public void setFloorNumber(int f){floorNumber = f;}
    public int getRowNumber(){return rowNumber;}
    public void setRowNumber(int r){rowNumber = r;}
    public int getSpotNumber(){return spotNumber;}
    public void setSpotNumber(int s){spotNumber = s;}
    public int getSpotType(){return spotType;}
    public void setSpotType(int st){spotType = st;}
    public int getSpotStatus(){return spotStatus;}
    public void setSpotStatus(int ss){spotStatus = ss;}
    public void assignSpot(){}
    public void releaseSpot(){}
    public void getAvailableSpots(){}
}