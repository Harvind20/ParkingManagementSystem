package ExitModule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Receipt {

    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String spotId;
    private String spotType;
    private String vehicleType;
    private double hoursParked;
    private double parkingFee;
    private double finesPaidNow;
    private double totalFinesOutstanding;
    private double parkingFeePaid;
    private double finesPaid;
    private double totalPaid;
    private double change;
    private String paymentMethod;
    private String receiptNumber;
    private String ticketId;
    private boolean paymentSuccess;

    public Receipt(String licensePlate, LocalDateTime entryTime, LocalDateTime exitTime,
                   String spotId, String spotType, String vehicleType, double hoursParked,
                   double parkingFee, double finesPaidNow, double totalFinesOutstanding,
                   double parkingFeePaid, double finesPaid, double totalPaid, double change,
                   String paymentMethod, String ticketId, boolean paymentSuccess) {

        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.spotId = spotId;
        this.spotType = spotType;
        this.vehicleType = vehicleType;
        this.hoursParked = hoursParked;
        this.parkingFee = parkingFee;
        this.finesPaidNow = finesPaidNow;
        this.totalFinesOutstanding = totalFinesOutstanding;
        this.parkingFeePaid = parkingFeePaid;
        this.finesPaid = finesPaid;
        this.totalPaid = totalPaid;
        this.change = change;
        this.paymentMethod = paymentMethod;
        this.ticketId = ticketId;
        this.paymentSuccess = paymentSuccess;
        
        // Generate receipt number
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String plateSuffix = licensePlate.length() > 4 ? 
            licensePlate.substring(0, 4) : licensePlate;
        this.receiptNumber = "RCP-" + exitTime.format(dtf) + "-" + plateSuffix;
    }

    // --- GETTERS REQUIRED BY RECEIPT DAO ---
    public String getTicketID() { return ticketId; }
    public String getLicensePlate() { return licensePlate; }
    public String getSpotId() { return spotId; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public double getHoursParked() { return hoursParked; }
    public double getParkingFee() { return parkingFee; }
    
    // Maps "getFines" to the fines paid in this specific receipt
    public double getFines() { return finesPaidNow; } 
    
    // Maps "getAmountPaid" to the total paid (fee + fines)
    public double getAmountPaid() { return totalPaid; }
    
    // Additional Getters
    public String getPaymentMethod() { return paymentMethod; }
    public double getTotalFinesOutstanding() { return totalFinesOutstanding; }
    public boolean isPaymentSuccess() { return paymentSuccess; }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("=".repeat(58)).append("\n");
        sb.append("                   PARKING LOT RECEIPT\n");
        sb.append("=".repeat(58)).append("\n");
        sb.append(String.format("Receipt #:    %s\n", receiptNumber));
        sb.append(String.format("Ticket #:     %s\n", ticketId));
        sb.append("-".repeat(58)).append("\n");

        sb.append(String.format("Vehicle:       %-12s (%s)\n", licensePlate, vehicleType));
        sb.append(String.format("Spot:          %-12s [%s]\n", spotId, spotType));
        sb.append(String.format("Entry:         %s\n", entryTime.format(dtf)));
        sb.append(String.format("Exit:          %s\n", exitTime.format(dtf)));
        sb.append(String.format("Duration:      %.1f hours\n", hoursParked));

        sb.append("-".repeat(58)).append("\n");

        sb.append(String.format("Parking Fee:  RM %11.2f\n", parkingFee));
        
        if (finesPaidNow > 0) {
            sb.append(String.format("Fines Paid:   RM %11.2f\n", finesPaidNow));
        }
        
        sb.append(String.format("Total Paid:   RM %11.2f\n", totalPaid));
        
        if (change > 0) {
            sb.append(String.format("Change:       RM %11.2f\n", change));
        }
        
        sb.append("-".repeat(58)).append("\n");
        
        double remainingFines = totalFinesOutstanding - finesPaidNow;
        if (remainingFines > 0.01) {
            sb.append(String.format("Outstanding Fines: RM %.2f\n", remainingFines));
            sb.append("These fines will be carried to your next visit.\n");
            sb.append("-".repeat(58)).append("\n");
        }
        
        sb.append(String.format("Payment:       %s\n", paymentMethod));
        sb.append("-".repeat(58)).append("\n");
        sb.append("Status:        PAID\n");
        sb.append("\nThank you for parking with us!\n");
        sb.append("=".repeat(58)).append("\n");
        
        return sb.toString();
    }
}