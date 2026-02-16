package EntryModule;

public class Car extends Vehicle {
    public Car(String plate) { 
        super(plate); // pass license plate to parent Vehicle class
        this.vehicleType = "Car"; // set specific vehicle type label
    }
}