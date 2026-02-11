package coreParkingSystem;

public class ParkingSpot {
    private String spotID;
    private Type spotType;
    private Status spotStatus = Status.AVAILABLE;
    private int spotNumber;
    public enum Type{COMPACT,REGULAR,HANDICAPPED,RESERVED}
    public enum Status{AVAILABLE,OCCUPIED}

    public ParkingSpot(String id, Type sType, int num){
        spotID = id; spotType = sType;
        spotNumber = num;
    }
    public String getSpotID(){return spotID;}
    public void setSpotID(String id){spotID = id;}
    public Type getSpotType(){return spotType;}
    public void setSpotType(Type type){spotType = type;}
    public Status getSpotStatus(){return spotStatus;}
    public void setSpotStatus(Status status){spotStatus = status;}
    public int getSpotNumber(){return spotNumber;}
    public void setSpotNumber(int num){spotNumber = num;}
}
