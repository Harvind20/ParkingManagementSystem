package EntryModule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketID;
    private String licensePlate;
    private String spotID;
    private String vehicleType;
    private String spotType;
    private LocalDateTime entryTime;
    private int sequenceNumber; // New field for AutoIncrement

    private Ticket(TicketBuilder builder) {
        this.licensePlate = builder.licensePlate;
        this.spotID = builder.spotID;
        this.vehicleType = builder.vehicleType;
        this.spotType = builder.spotType;
        this.entryTime = builder.entryTime;
        this.sequenceNumber = builder.sequenceNumber;
        this.ticketID = generateID();
    }

    // Constructor for Testing (Updated)
    public Ticket(String plate, String vType, String sId, String sType, LocalDateTime time, int seq) {
        this.licensePlate = plate;
        this.vehicleType = vType;
        this.spotID = sId;
        this.spotType = sType;
        this.entryTime = time;
        this.sequenceNumber = seq;
        this.ticketID = generateID();
    }

    private String generateID() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        // Format: T<Seq>-<Plate>-<Time>
        return "T" + sequenceNumber + "-" + licensePlate + "-" + entryTime.format(dtf);
    }

    public String getTicketID() { return ticketID; }
    public String getLicensePlate() { return licensePlate; }
    public String getSpotId() { return spotID; }
    public String getVehicleType() { return vehicleType; }
    public String getSpotType() { return spotType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public int getSequenceNumber() { return sequenceNumber; }

    public void setTicketID(String id) { this.ticketID = id; }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketID + " | Spot: " + spotID + " | Type: " + vehicleType;
    }

    public static class TicketBuilder {
        private String licensePlate;
        private String spotID;
        private String vehicleType = "Car";
        private String spotType = "Regular";
        private LocalDateTime entryTime;
        private int sequenceNumber = 1; // Default to 1

        public TicketBuilder addPlate(String plate) {
            this.licensePlate = plate;
            return this;
        }

        public TicketBuilder assignSpot(String spotID) {
            this.spotID = spotID;
            return this;
        }

        public TicketBuilder addVehicleType(String type) {
            this.vehicleType = type;
            return this;
        }

        public TicketBuilder addSpotType(String type) {
            this.spotType = type;
            return this;
        }

        public TicketBuilder addTime(long timeInMillis) {
            this.entryTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeInMillis), 
                ZoneId.systemDefault()
            );
            return this;
        }

        public TicketBuilder addTime(LocalDateTime time) {
            this.entryTime = time;
            return this;
        }
        
        public TicketBuilder addSequenceNumber(int seq) {
            this.sequenceNumber = seq;
            return this;
        }

        public Ticket build() {
            if (entryTime == null) {
                this.entryTime = LocalDateTime.now();
            }
            return new Ticket(this);
        }
    }
}