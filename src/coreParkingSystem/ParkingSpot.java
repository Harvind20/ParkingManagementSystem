package coreParkingSystem;

public class ParkingSpot {
    private String spotID;
    private String spotType;
    private String spotStatus;
    private int spotNumber;

    public String getSpotID(){return spotID;}
    public void setSpotID(String id){spotID = id;}
    public String getSpotType(){return spotType;}
    public void setSpotType(String type){spotType = type;}
    public String getSpotStatue(){return spotStatus;}
    public void setSpotStatus(String status){spotStatus = status;}
    public int getSpotNumber(){return spotNumber;}
    public void setSpotNumber(int num){spotNumber = num;}
}
