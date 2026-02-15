package UserInterface;
import coreParkingSystem.DatabaseConnection;
import coreParkingSystem.ParkingLot;
import coreParkingSystem.ParkingSpot;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;


public class ParkingRowBuilder {

    public static JPanel createRow(
            int floor,
            int rowIndex,
            boolean vipEnabled,
            boolean handicapEnabled,
            SpotSelectionUI ui,
            JPanel[] selectedSpotHolder
    ){
        System.out.println("createRow() CALLED");
        JPanel row = new JPanel(new GridLayout(1,10,0,0));
        row.setOpaque(false);

        for(int i=0;i<10;i++){
            int spotNumber = i + 1;

            String dbSpotId = floor + "-" + rowIndex + "-" + spotNumber;

            boolean occupied = isSpotOccupied(dbSpotId);


          
            String spotIdBackend = floor + "-" + rowIndex + "-" + spotNumber;

            ParkingLot lot = ParkingLot.getInstance();
            ParkingSpot.Status status = lot.getSpotStatus(spotIdBackend);
            ParkingSpot.Type typeEnum = lot.getSpotType(spotIdBackend);
            
            String typeString = (typeEnum != null) ? typeEnum.toString() : getMockType(floor, i);
            boolean isOccupied = occupied;

            Color c;
            if(isOccupied) {
                c = Color.RED;
            } else if(typeString.equals("COMPACT")) {
                c = new Color(30,100,20);
            } else if(typeString.equals("REGULAR")) {
                c = new Color(0,180,90);
            } else if(typeString.equals("VIP") || typeString.equals("RESERVED")) {
                c = new Color(140,30,160);
            } else {
                c = new Color(70,160,220); // HANDICAPPED
            }

            JPanel spot = new JPanel();
            if (occupied) {
                spot.setBackground(Color.RED);
            } else {
                spot.setBackground(c);
            }
            spot.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));

            boolean isAllowed = true;
            if(isOccupied) isAllowed = false;
            if((typeString.equals("HANDICAPPED") || typeString.equals("HANDICAP")) && !handicapEnabled) isAllowed = false;
            if((typeString.equals("VIP") || typeString.equals("RESERVED")) && !vipEnabled) isAllowed = false;

            if(!isAllowed) {
                spot.setEnabled(false);
                if(!isOccupied) spot.setBackground(c.darker());
            }

            if(ui != null){
                spot.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {

                        if(!spot.isEnabled()) return;

                        if(selectedSpotHolder[0] != null)
                            selectedSpotHolder[0].setBorder(
                                BorderFactory.createLineBorder(Color.WHITE,1)
                            );

                        spot.setBorder(BorderFactory.createLineBorder(Color.YELLOW,3));
                        selectedSpotHolder[0] = spot;
                        new ConfirmSpotDialog(
                            ui,
                            "Floor " + floor +
                            " Row " + rowIndex +
                            " Spot " + spotNumber
                        ).setVisible(true);
                    }
                });
            }

            row.add(spot);
        }

        return row;
    }

    private static String getMockType(int floor, int col){
        if(floor == 1){
            if(col < 3) return "COMPACT";
            if(col < 6) return "REGULAR";
            if(col < 8) return "VIP";
            return "HANDICAPPED";
        }
        if(floor == 2){
            if(col < 5) return "REGULAR";
            if(col < 8) return "VIP";
            return "HANDICAPPED";
        }
        if(col % 2 == 0) return "REGULAR";
        return "COMPACT";
    }

    private static boolean isSpotOccupied(String spotId) {
    boolean occupied = false;

    try {
        Connection conn = DatabaseConnection.connect();

        String sql = "SELECT status FROM parking_spots WHERE spot_id=?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, spotId);

        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String status = rs.getString("status");
            if ("OCCUPIED".equalsIgnoreCase(status)) {
                occupied = true;
            }
        }

        rs.close();
        pst.close();
        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
    }

    return occupied;
    }

}

