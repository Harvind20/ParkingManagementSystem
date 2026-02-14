package UserInterface;
import javax.swing.*;
import java.awt.*;

public class ParkingRowBuilder {

    public static JPanel createRow(
            int floor,
            int rowIndex,
            boolean vipEnabled,
            boolean handicapEnabled,
            SpotSelectionUI ui,
            JPanel[] selectedSpotHolder
    ){
        JPanel row = new JPanel(new GridLayout(1,10,0,0));
        row.setOpaque(false);

        for(int i=0;i<10;i++){

            String type;
            if(i < 3) type = "COMPACT";
            else if(i < 6) type = "REGULAR";
            else if(i < 8) type = "VIP";
            else type = "HANDICAP";

            Color c;
            if(type.equals("COMPACT")) c = new Color(30,100,20);
            else if(type.equals("REGULAR")) c = new Color(0,180,90);
            else if(type.equals("VIP")) c = new Color(140,30,160);
            else c = new Color(70,160,220); // HANDICAP

            JPanel spot = new JPanel();
            spot.setBackground(c);
            spot.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));

            if(type.equals("HANDICAP") && !handicapEnabled)
                spot.setEnabled(false);

            if(type.equals("VIP") && !vipEnabled)
                spot.setEnabled(false);

            int spotNumber = i + 1;

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
                            "Floor " + ui.currentFloor +
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
}
