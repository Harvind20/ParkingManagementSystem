package EntryModule;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Ticket {
    private String ticketID;
    private String licensePlate;
    private String vehicleType;
    private LocalDateTime entryTime;
    private int sequenceNumber;
    private String fineSchemeAtEntry;

    private Ticket(TicketBuilder builder) {
        this.licensePlate = builder.licensePlate;
        this.vehicleType = builder.vehicleType;
        this.entryTime = builder.entryTime;
        this.sequenceNumber = builder.sequenceNumber;
        this.fineSchemeAtEntry = builder.fineSchemeAtEntry;
        this.ticketID = generateID();
    }

    // Constructor for Testing (Updated)
    public Ticket(String plate, String vType, String sId, String sType, LocalDateTime time, int seq) {
        this.licensePlate = plate;
        this.vehicleType = vType;
        this.entryTime = time;
        this.sequenceNumber = seq;
        this.fineSchemeAtEntry = "FIXED";
        this.ticketID = generateID();
    }

    private String generateID() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        return "T" + sequenceNumber + "-" + licensePlate + "-" + entryTime.format(dtf);
    }

    public String getTicketID() { return ticketID; }
    public String getLicensePlate() { return licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public int getSequenceNumber() { return sequenceNumber; }
    public String getFineSchemeAtEntry() { return fineSchemeAtEntry; }

    public void setTicketID(String id) { this.ticketID = id; }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketID + " | Type: " + vehicleType;
    }

    public static class TicketBuilder {
        private String licensePlate;
        private String vehicleType = "Car";
        private LocalDateTime entryTime;
        private int sequenceNumber = 1;
        private String fineSchemeAtEntry = "FIXED";

        public TicketBuilder addPlate(String plate) {
            this.licensePlate = plate;
            return this;
        }

        public TicketBuilder addVehicleType(String type) {
            this.vehicleType = type;
            return this;
        }
        
        public TicketBuilder addFineScheme(String scheme) {
            this.fineSchemeAtEntry = scheme;
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