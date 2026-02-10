package coreParkingSystem;

public class ParkingSpot {
    private String spotID;
    private Global.Type spotType;
    private Global.Status spotStatus = Global.Status.AVAILABLE;
    private int spotNumber;

    public ParkingSpot(String id, Global.Type sType, int num){
        spotID = id; spotType = sType;
        spotNumber = num;
    }
    public String getSpotID(){return spotID;}
    public void setSpotID(String id){spotID = id;}
    public Global.Type getSpotType(){return spotType;}
    public void setSpotType(Global.Type type){spotType = type;}
    public Global.Status getSpotStatus(){return spotStatus;}
    public void setSpotStatus(Global.Status status){spotStatus = status;}
    public int getSpotNumber(){return spotNumber;}
    public void setSpotNumber(int num){spotNumber = num;}
}
