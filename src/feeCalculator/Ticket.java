package feeCalculator;

import java.time.LocalDateTime;

public class Ticket {
    private String licensePlate;
    private String vehicleType;
    private String spotId;
    private String spotType;
    private LocalDateTime entryTime;

    public Ticket(String licensePlate, String vehicleType, String spotId,
                String spotType, LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.spotId = spotId;
        this.spotType = spotType;
        this.entryTime = entryTime;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public String getSpotId() { return spotId; }
    public String getSpotType() { return spotType; }
    public LocalDateTime getEntryTime() { return entryTime; }
}
