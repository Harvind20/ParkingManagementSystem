package EntrySystem;

public class Main {
    public static void main(String[] args) {
        EntryController controller = new EntryController();

        System.out.println("--- Starting Integrated Entry System Test ---");

        if (controller.checkSystemFull()) {
            System.out.println("Parking Full");
            return;
        }

        System.out.println("\nTest 2: SUV entering Compact Spot (1-1-4)");
        Vehicle mySuv = new SUV("JEEP-99");
        String result1 = controller.attemptPark(mySuv, "1-1-4"); 
        System.out.println(result1);

        System.out.println("\nTest 3: Double Parking Test (1-1-1)");
        Vehicle car1 = new Car("FIRST-1");
        System.out.println(controller.attemptPark(car1, "1-1-1"));
        
        Vehicle car2 = new Car("SECOND-2");
        System.out.println(controller.attemptPark(car2, "1-1-1"));

        System.out.println("\nTest 4: Handicapped entering Regular Spot (1-1-2)");
        Vehicle myHandi = new HandicappedVehicle("OKU-555");
        String result3 = controller.attemptPark(myHandi, "1-1-2"); 
        System.out.println(result3);
        
        System.out.println("\nTest 5: Regular Car in Reserved Spot (1-1-9)");
        Vehicle regCar = new Car("MYVI-88");
        String result4 = controller.attemptPark(regCar, "1-1-9");
        System.out.println(result4);
    }
}