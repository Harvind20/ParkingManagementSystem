package EntryModule;

public class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plate) { 
        super(plate); // pass license plate to parent Vehicle class
        this.vehicleType = "Handicapped"; // set specific vehicle type label
    }
}