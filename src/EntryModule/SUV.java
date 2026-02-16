package EntryModule;

public class SUV extends Vehicle {
    public SUV(String plate) { 
        super(plate); // pass license plate to parent Vehicle class
        this.vehicleType = "SUV"; // set specific vehicle type label
    }
}