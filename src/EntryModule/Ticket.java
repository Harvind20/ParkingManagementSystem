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

    // private constructor forces object creation through the builder pattern
    private Ticket(TicketBuilder builder) {
        this.licensePlate = builder.licensePlate;
        this.vehicleType = builder.vehicleType;
        this.entryTime = builder.entryTime;
        this.sequenceNumber = builder.sequenceNumber;
        this.fineSchemeAtEntry = builder.fineSchemeAtEntry;
        this.ticketID = generateID(); // generate unique ID once all data is set
    }

    // constructor mainly used for testing or manual ticket creation
    public Ticket(String plate, String vType, String sId, String sType, LocalDateTime time, int seq) {
        this.licensePlate = plate;
        this.vehicleType = vType;
        this.entryTime = time;
        this.sequenceNumber = seq;
        this.fineSchemeAtEntry = "FIXED";
        this.ticketID = generateID();
    }

    // builds ticket ID using sequence number and plate +and entry time
    // ensures ID is unique and traceable
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

        // sets plate number for ticket
        public TicketBuilder addPlate(String plate) {
            this.licensePlate = plate;
            return this;
        }

        // allows vehicle type to be customized if needed
        public TicketBuilder addVehicleType(String type) {
            this.vehicleType = type;
            return this;
        }
        
        // stores which fine scheme was active at the time of entry
        public TicketBuilder addFineScheme(String scheme) {
            this.fineSchemeAtEntry = scheme;
            return this;
        }

        // converts system milliseconds into LocalDateTime
        public TicketBuilder addTime(long timeInMillis) {
            this.entryTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeInMillis), 
                ZoneId.systemDefault()
            );
            return this;
        }

        // directly set entry time using LocalDateTime
        public TicketBuilder addTime(LocalDateTime time) {
            this.entryTime = time;
            return this;
        }
        
        // assigns sequence number to help keep ticket IDs unique
        public TicketBuilder addSequenceNumber(int seq) {
            this.sequenceNumber = seq;
            return this;
        }

        // builds final Ticket object
        // ensures entryTime is never null
        public Ticket build() {
            if (entryTime == null) {
                this.entryTime = LocalDateTime.now();
            }
            return new Ticket(this);
        }
    }
}
