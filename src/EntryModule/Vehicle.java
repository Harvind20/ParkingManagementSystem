package EntryModule;

public abstract class Vehicle {
    protected String licensePlate;
    protected String vehicleType;
    protected long entryTime;
    protected String ticketId;

    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
        this.entryTime = System.currentTimeMillis();
    }

    public String getLicensePlate() { return licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public long getEntryTime() { return entryTime; }
    public void setTicketId(String tId) { this.ticketId = tId; }
}