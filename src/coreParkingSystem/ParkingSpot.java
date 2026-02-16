package coreParkingSystem;

public class ParkingSpot {

    // formatted as Floor-Row-Spot 
    private String spotID;

    // determines parking rules and violations
    private Type spotType;

    // current occupancy state of the spot
    private Status spotStatus = Status.AVAILABLE;

    private int spotNumber;

    // stores plate number of vehicle currently using this spot
    private String currentlyParkedVehicleID = null;

    public enum Type { COMPACT, REGULAR, HANDICAPPED, RESERVED }
    public enum Status { AVAILABLE, OCCUPIED }

    public ParkingSpot(String id, Type sType, int num){
        spotID = id;
        spotType = sType;
        spotNumber = num;
    }

    public String getSpotID(){ return spotID; }
    public void setSpotID(String id){ spotID = id; }

    public Type getSpotType(){ return spotType; }
    public void setSpotType(Type type){ spotType = type; }

    public Status getSpotStatus(){ return spotStatus; }
    public void setSpotStatus(Status status){ spotStatus = status; }

    public int getSpotNumber(){ return spotNumber; }
    public void setSpotNumber(int num){ spotNumber = num; }

    public String getCurrentlyParkedVehicleID(){ return currentlyParkedVehicleID; }

    // links a vehicle to this spot when occupied
    public void setCurrentlyParkedVehicleID(String id){ currentlyParkedVehicleID = id; }
}
