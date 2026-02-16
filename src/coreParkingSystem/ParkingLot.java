package coreParkingSystem;

import EntryModule.Ticket;
import EntryModule.Vehicle;
import FineModule.FineManager;
import FineModule.FineScheme;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// central system controller managing floors, spots, tickets and fine updates
// implemented as a Singleton to ensure only one parking system instance exists
public class ParkingLot {

    private ArrayList<Floor> floors = new ArrayList<>();
    private TicketDAO ticketDAO = new TicketDAO(); 
    private ParkingSpotDAO spotDAO = new ParkingSpotDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    // scheduler used to automatically recalculate fines over time
    private ScheduledExecutorService fineScheduler;
    
    final int floorNumber = 3;

    // private constructor for Singleton pattern
    private ParkingLot(){
        DatabaseConnection.initializeDB(); 
        initializeFloors();
        startFineUpdateScheduler();
    }
    
    private static class InstanceHolder{
        private static final ParkingLot INSTANCE = new ParkingLot();
    }
    
    public static ParkingLot getInstance(){
        return InstanceHolder.INSTANCE;
    }

    // starts background task that updates fines every hour
    private void startFineUpdateScheduler() {
        fineScheduler = Executors.newSingleThreadScheduledExecutor();
        fineScheduler.scheduleAtFixedRate(this::updateAllFines, 1, 1, TimeUnit.HOURS);
        System.out.println("[System] Hourly Fine Scheduler Started.");
    }

    public void stopScheduler() {
        if (fineScheduler != null && !fineScheduler.isShutdown()) {
            fineScheduler.shutdown();
        }
    }

    // iterates through all occupied spots and recalculates fines
    private void updateAllFines() {
        System.out.println("[System] Running Hourly Fine Update...");
        LocalDateTime now = LocalDateTime.now();

        for (Floor floor : floors) {
            for (Row row : floor.getRows()) {
                for (ParkingSpot spot : row.getSpots()) {
                    if (spot.getSpotStatus() == ParkingSpot.Status.OCCUPIED) {
                        String plate = spot.getCurrentlyParkedVehicleID();
                        if (plate != null) {
                            processFineUpdateForVehicle(plate, spot, now);
                        }
                    }
                }
            }
        }
    }

    // calculates updated fine for a specific vehicle based on duration and violations
    private void processFineUpdateForVehicle(String plate, ParkingSpot spot, LocalDateTime now) {
        Ticket ticket = ticketDAO.findActiveByPlate(plate);
        if (ticket == null) return;

        String schemeName = ticket.getFineSchemeAtEntry();
        FineScheme scheme = FineManager.getSchemeByName(schemeName);

        Duration duration = Duration.between(ticket.getEntryTime(), now);
        int hoursParked = (int) Math.ceil(duration.toMinutes() / 60.0);
        if (hoursParked <= 0) hoursParked = 1;

        boolean isViolation = false;
        String violationReason = "";
        
        boolean isVip = checkVipStatus(plate);

        // check reserved spot violation
        if (spot.getSpotType() == ParkingSpot.Type.RESERVED && !isVip) {
            isViolation = true;
            violationReason = "Violation: Non-VIP in Reserved Spot";
        } 
        // check handicapped spot violation
        else if (spot.getSpotType() == ParkingSpot.Type.HANDICAPPED && !ticket.getVehicleType().equalsIgnoreCase("Handicapped")) {
            isViolation = true;
            violationReason = "Violation: Unauthorized in Handicap Spot";
        }

        FineManager fm = new FineManager();
        double newFineAmount = 0.0;

        // violation fines are applied immediately
        if (isViolation) {
            newFineAmount = fm.calculateFine(scheme, hoursParked, true);
        } 
        // otherwise apply overstay fine after grace period
        else {
            newFineAmount = fm.calculateFine(scheme, hoursParked, false);
            violationReason = "Overstay Fee (>24h)";
        }

        if (newFineAmount > 0) {
            updateFineInDB(plate, newFineAmount, violationReason);
        }
    }

    // updates existing unpaid fine or inserts new fine if none exists
    private void updateFineInDB(String plate, double amount, String reason) {
        String checkSql = "SELECT fine_id FROM fines WHERE plate_num = ? AND status = 'UNPAID'";
        String updateSql = "UPDATE fines SET amount = ?, reason = ? WHERE fine_id = ?";
        String insertSql = "INSERT INTO fines(plate_num, amount, reason, status, date_issued) VALUES(?,?,?,?,datetime('now'))";

        try (Connection conn = DatabaseConnection.connect()) {

            // check if an unpaid fine already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, plate);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // update existing fine
                    int fineId = rs.getInt("fine_id");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, amount);
                        updateStmt.setString(2, reason);
                        updateStmt.setInt(3, fineId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    // insert new fine record
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, plate);
                        insertStmt.setDouble(2, amount);
                        insertStmt.setString(3, reason);
                        insertStmt.setString(4, "UNPAID");
                        insertStmt.executeUpdate();
                    }
                }
            }

            // sync accumulated fine total for vehicle
            vehicleDAO.syncTotalFines(plate);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // checks VIP status from database for reserved spot validation
    private boolean checkVipStatus(String plate) {
        String sql = "SELECT is_vip FROM vehicles WHERE plate_num = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getBoolean("is_vip");
        } catch (Exception e) {}
        return false;
    }

    // persists vehicle details
    public void saveVehicle(Vehicle v) {
        vehicleDAO.create(v);
        System.out.println("[DB] Vehicle saved: " + v.getLicensePlate());
    }

    // generates next ticket sequence number for same plate
    public int getNextSequenceNumber(String plateNum) {
        return ticketDAO.getTicketCount(plateNum) + 1;
    }
    
    // saves new ticket record
    public void saveTicket(Ticket ticket) {
        if (ticket != null) {
            ticketDAO.create(ticket); 
            System.out.println("[DB] Ticket saved for " + ticket.getLicensePlate());
        }
    }

    // retrieves active ticket for a vehicle
    public Ticket getTicketByPlate(String plate) {
        return ticketDAO.findActiveByPlate(plate);
    }

    // marks ticket as completed during exit
    public void closeTicket(String ticketId, String plate) {
        ticketDAO.closeTicket(ticketId); 
        System.out.println("[DB] Ticket marked COMPLETED for " + plate);
    }

    public void removeTicket(String plate) {
        Ticket t = getTicketByPlate(plate);
        if(t != null) closeTicket(t.getTicketID(), plate);
    }

    // builds parking structure and inserts spots into database
    private void initializeFloors(){
        for(int i = 0; i < floorNumber; i++){
            Floor floor = new Floor(i+1);
            floors.add(floor);
            for(Row row : floor.getRows()) {
                for(ParkingSpot spot : row.getSpots()) {
                    spotDAO.create(spot); 
                }
            }
        }
        System.out.println("[System] Parking Spots initialized in Database.");
    }
    
    public ArrayList<Floor> getFloors(){ return floors; }
    
    // updates spot status and persists change
    public void setSpotStatus(String sID, ParkingSpot.Status status){
        ParkingSpot spot = getSpotById(sID);
        if (spot != null) {
            spot.setSpotStatus(status);
            spotDAO.update(spot);
        }
    }

    // retrieves spot status
    public ParkingSpot.Status getSpotStatus(String sID){
        ParkingSpot spot = spotDAO.read(sID);
        return (spot != null) ? spot.getSpotStatus() : null;
    }
    
    public ParkingSpot.Type getSpotType(String sID){
        ParkingSpot spot = getSpotById(sID);
        return (spot != null) ? spot.getSpotType() : null;
    }

    // converts string ID floor-row-spot into actual object reference
    public ParkingSpot getSpotById(String sID) {
        String[] idData = sID.split("\\-");
        if(idData.length != 3) return null;
        try {
            int f = Integer.parseInt(idData[0]) - 1;
            int r = Integer.parseInt(idData[1]) - 1;
            int s = Integer.parseInt(idData[2]) - 1;
            if (f >= 0 && f < floors.size()) {
                return floors.get(f).getRow(r).getSpot(s);
            }
        } catch (Exception e) {}
        return null;
    }

    // updates which vehicle is occupying the spot
    public void updateSpotOccupancy(ParkingSpot spot){
        spotDAO.updateCurrentlyParkedVehicle(spot);
    }
}
