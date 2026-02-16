package EntryModule;

public class Motorcycle extends Vehicle {
    public Motorcycle(String plate) { 
        super(plate); // pass license plate to parent Vehicle class
        this.vehicleType = "Motorcycle"; // set specific vehicle type label
    }
}