package EntryModule;

public abstract class Vehicle {
    protected String licensePlate;
    protected String vehicleType;
    protected long entryTime;
    protected String ticketId;
    protected boolean isVip;

    // base constructor used by all vehicle subclasses
    // stores plate and records the entry timestamp at creation time
    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
        this.entryTime = System.currentTimeMillis();
        this.isVip = false;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public long getEntryTime() { return entryTime; }

    // links the generated ticket ID to this vehicle after parking
    public void setTicketId(String tId) { this.ticketId = tId; }

    // marks whether the vehicle has VIP privileges which affects spot rules and fines
    public void setVip(boolean vip) {
        this.isVip = vip;
    }

    public boolean isVip() {
        return isVip;
    }
}
