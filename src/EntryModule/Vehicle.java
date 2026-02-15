package EntryModule;

public abstract class Vehicle {
    protected String licensePlate;
    protected String vehicleType;
    protected long entryTime;
    protected String ticketId;
    protected boolean isVip;

    public Vehicle(String licensePlate) {
        this.licensePlate = licensePlate;
        this.entryTime = System.currentTimeMillis();
        this.isVip = false;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getVehicleType() { return vehicleType; }
    public long getEntryTime() { return entryTime; }
    public void setTicketId(String tId) { this.ticketId = tId; }

    public void setVip(boolean vip) {
        this.isVip = vip;
    }

    public boolean isVip() {
        return isVip;
    }
}