package feeCalculator;

import java.time.LocalDateTime;

public class Receipt {

    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String spotId;
    private String spotType;
    private String vehicleType;
    private double hoursParked;
    private double parkingFee;
    private double fines;
    private double totalDue;
    private double amountPaid;
    private boolean paymentSuccess;

    public Receipt(String licensePlate, LocalDateTime entryTime, LocalDateTime exitTime,
                   String spotId, String spotType, String vehicleType, double hoursParked,
                   double parkingFee, double fines, double totalDue, double amountPaid,
                   boolean paymentSuccess) {

        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.spotId = spotId;
        this.spotType = spotType;
        this.vehicleType = vehicleType;
        this.hoursParked = hoursParked;
        this.parkingFee = parkingFee;
        this.fines = fines;
        this.totalDue = totalDue;
        this.amountPaid = amountPaid;
        this.paymentSuccess = paymentSuccess;
    }

    @Override
    public String toString() {
        String receipt = "\n" + "=".repeat(50) + "\n";
        receipt += "PARKING LOT RECEIPT\n";
        receipt += "=".repeat(50) + "\n";

        receipt += String.format("Vehicle: %s (%s)\n", licensePlate, vehicleType);
        receipt += String.format("Spot: %s (%s)\n", spotId, spotType);
        receipt += String.format("Entry: %s\n", entryTime);
        receipt += String.format("Exit: %s\n", exitTime);
        receipt += String.format("Duration: %.1f hours\n", hoursParked);

        receipt += "-".repeat(50) + "\n";

        receipt += String.format("Parking Fee: RM %.2f\n", parkingFee);
        receipt += String.format("Fines: RM %.2f\n", fines);
        receipt += String.format("Total Due: RM %.2f\n", totalDue);
        receipt += String.format("Amount Paid: RM %.2f\n", amountPaid);

        if (amountPaid > totalDue) {
            receipt += String.format("Change: RM %.2f\n", amountPaid - totalDue);
        }

        receipt += "-".repeat(50) + "\n";
        receipt += "Status: " + (paymentSuccess ? "PAID" : "UNPAID") + "\n";
        receipt += "=".repeat(50) + "\n";

        return receipt;
    }
}
