package EntrySystem;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ticket {
    private String ticketID;
    private String licensePlate;
    private String spotID;
    private long entryTime;

    private Ticket(TicketBuilder builder) {
        this.licensePlate = builder.licensePlate;
        this.spotID = builder.spotID;
        this.entryTime = builder.entryTime;
        this.ticketID = generateID();
    }

    private String generateID() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "Tix-" + licensePlate + "-" + sdf.format(new Date(entryTime));
    }

    @Override
    public String toString() {
        return "Ticket ID: " + ticketID + " | Spot: " + spotID;
    }

    public static class TicketBuilder {
        private String licensePlate;
        private String spotID;
        private long entryTime;

        public TicketBuilder addPlate(String plate) {
            this.licensePlate = plate;
            return this;
        }

        public TicketBuilder assignSpot(String spotID) {
            this.spotID = spotID;
            return this;
        }

        public TicketBuilder addTime(long time) {
            this.entryTime = time;
            return this;
        }

        public Ticket build() {
            return new Ticket(this);
        }
    }
}