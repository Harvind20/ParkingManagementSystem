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

public class ParkingLot {
    private ArrayList<Floor> floors = new ArrayList<>();
    private TicketDAO ticketDAO = new TicketDAO(); 
    private ParkingSpotDAO spotDAO = new ParkingSpotDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    private ScheduledExecutorService fineScheduler;
    
    final int floorNumber = 3;

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

        if (spot.getSpotType() == ParkingSpot.Type.RESERVED && !isVip) {
            isViolation = true;
            violationReason = "Violation: Non-VIP in Reserved Spot";
        } else if (spot.getSpotType() == ParkingSpot.Type.HANDICAPPED && !ticket.getVehicleType().equalsIgnoreCase("Handicapped")) {
            isViolation = true;
            violationReason = "Violation: Unauthorized in Handicap Spot";
        }

        FineManager fm = new FineManager();
        double newFineAmount = 0.0;

        if (isViolation) {
            newFineAmount = fm.calculateFine(scheme, hoursParked, true);
        } else {
            newFineAmount = fm.calculateFine(scheme, hoursParked, false);
            violationReason = "Overstay Fee (>24h)";
        }

        if (newFineAmount > 0) {
            updateFineInDB(plate, newFineAmount, violationReason);
        }
    }

    private void updateFineInDB(String plate, double amount, String reason) {
        String checkSql = "SELECT fine_id FROM fines WHERE plate_num = ? AND status = 'UNPAID'";
        String updateSql = "UPDATE fines SET amount = ?, reason = ? WHERE fine_id = ?";
        String insertSql = "INSERT INTO fines(plate_num, amount, reason, status, date_issued) VALUES(?,?,?,?,datetime('now'))";

        try (Connection conn = DatabaseConnection.connect()) {
            // Check existence
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, plate);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int fineId = rs.getInt("fine_id");
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setDouble(1, amount);
                        updateStmt.setString(2, reason);
                        updateStmt.setInt(3, fineId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, plate);
                        insertStmt.setDouble(2, amount);
                        insertStmt.setString(3, reason);
                        insertStmt.setString(4, "UNPAID");
                        insertStmt.executeUpdate();
                    }
                }
            }
            vehicleDAO.syncTotalFines(plate);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    public void saveVehicle(Vehicle v) {
        vehicleDAO.create(v);
        System.out.println("[DB] Vehicle saved: " + v.getLicensePlate());
    }


    public int getNextSequenceNumber(String plateNum) {
        return ticketDAO.getTicketCount(plateNum) + 1;
    }
    
    public void saveTicket(Ticket ticket) {
        if (ticket != null) {
            ticketDAO.create(ticket); 
            System.out.println("[DB] Ticket saved for " + ticket.getLicensePlate());
        }
    }

    public Ticket getTicketByPlate(String plate) {
        return ticketDAO.findActiveByPlate(plate);
    }

    public void closeTicket(String ticketId, String plate) {
        ticketDAO.closeTicket(ticketId); 
        System.out.println("[DB] Ticket marked COMPLETED for " + plate);
    }

    public void removeTicket(String plate) {
        Ticket t = getTicketByPlate(plate);
        if(t != null) closeTicket(t.getTicketID(), plate);
    }

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
    
    public void setSpotStatus(String sID, ParkingSpot.Status status){
        ParkingSpot spot = getSpotById(sID);
        if (spot != null) {
            spot.setSpotStatus(status);
            spotDAO.update(spot);
        }
    }

    public ParkingSpot.Status getSpotStatus(String sID){
        ParkingSpot spot = spotDAO.read(sID);
        return (spot != null) ? spot.getSpotStatus() : null;
    }
    
    public ParkingSpot.Type getSpotType(String sID){
        ParkingSpot spot = getSpotById(sID);
        return (spot != null) ? spot.getSpotType() : null;
    }

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

    public void updateSpotOccupancy(ParkingSpot spot){
        spotDAO.updateCurrentlyParkedVehicle(spot);
    }
}