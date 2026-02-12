import javax.swing.*;
import java.awt.*;

public class ParkingRowBuilder {

    public static JPanel createRow(
            int rowIndex,
            boolean vipEnabled,
            boolean handicapEnabled,
            SpotSelectionUI ui,
            JPanel[] selectedSpotHolder
    ){
        JPanel row = new JPanel(new GridLayout(1,10,0,0));
        row.setOpaque(false);

        for(int i=0;i<10;i++){

            Color c;
            String type;

            if(i<3){ c=new Color(30,100,20); type="COMPACT"; }
            else if(i<6){ c=new Color(0,180,90); type="REGULAR"; }
            else if(i<8){ c=new Color(140,30,160); type="VIP"; }
            else{ c=new Color(70,160,220); type="HANDICAP"; }

            JPanel spot = new JPanel();
            spot.setBackground(c);
            spot.setBorder(BorderFactory.createLineBorder(ThemeColors.SECONDARY,1));

            if(type.equals("HANDICAP") && !handicapEnabled)
                spot.setEnabled(false);

            if(type.equals("VIP") && !vipEnabled)
                spot.setEnabled(false);

            int spotNumber = i + 1;

            spot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(!spot.isEnabled()) return;

                    // Remove previous highlight
                    if(selectedSpotHolder[0] != null)
                        selectedSpotHolder[0].setBorder(
                            BorderFactory.createLineBorder(ThemeColors.SECONDARY,1)
                        );

                    // Highlight selected tile
                    spot.setBorder(BorderFactory.createLineBorder(Color.YELLOW,3));
                    selectedSpotHolder[0] = spot;

                    // Use Current floor value from SpotSelectionUI
                    new ConfirmSpotDialog(
                        ui,
                        "Floor " + ui.currentFloor +
                        " Row " + rowIndex +
                        " Spot " + spotNumber
                    ).setVisible(true);
                }
            });

            row.add(spot);
        }

        return row;
    }
}
