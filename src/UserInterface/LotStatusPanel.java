package UserInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class LotStatusPanel extends JPanel {

    private JPanel topBlock, bottomBlock;
    private JComboBox<String> floorDropdown;

    private JPanel legendOccupied, legendRegular, legendCompact, legendVIP, legendHandicap;

    private int rowCounter = 1;
    private int currentFloor = 1;

    boolean handicapEnabled = true;
    boolean vipEnabled = true;

    JPanel[] selectedSpotHolder = new JPanel[1];

    public LotStatusPanel() {

        setLayout(null);
        setBackground(ThemeColors.PRIMARY);

        // Floor dropdown option
        floorDropdown = new JComboBox<>(new String[]{
                "1st Floor",
                "2nd Floor",
                "3rd Floor"
        });

        RoundedPanel floorWrapper = new RoundedPanel(25);
        floorWrapper.setBackground(ThemeColors.SECONDARY);
        floorWrapper.setLayout(new BorderLayout());
        floorWrapper.add(floorDropdown);
        add(floorWrapper);

        floorDropdown.addActionListener(e -> {
            String selected = (String) floorDropdown.getSelectedItem();

            if(selected.equals("1st Floor")) currentFloor = 1;
            else if(selected.equals("2nd Floor")) currentFloor = 2;
            else currentFloor = 3;

            reloadGrid();
        });

        topBlock = createRowBlock();
        bottomBlock = createRowBlock();

        add(topBlock);
        add(bottomBlock);

        legendOccupied = createLegendBox(Color.RED, "Occupied");
        legendRegular = createLegendBox(new Color(0,180,90), "Regular");
        legendCompact = createLegendBox(new Color(30,100,20), "Compact");
        legendVIP = createLegendBox(new Color(140,30,160), "Reserved");
        legendHandicap = createLegendBox(new Color(70,160,220), "Handicapped");

        add(legendOccupied);
        add(legendRegular);
        add(legendCompact);
        add(legendVIP);
        add(legendHandicap);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {

                int panelW = getWidth();
                int panelH = getHeight();

                int blockW = 700;
                int blockH = 180;

                int topX = panelW/2 - blockW/2;
                int topY = panelH/2 - 220;

                int bottomX = topX;
                int bottomY = panelH/2 + 20;

                topBlock.setBounds(topX, topY, blockW, blockH);
                bottomBlock.setBounds(bottomX, bottomY, blockW, blockH);

                floorWrapper.setBounds(30, 20, 200, 40);

                int ly = panelH/2 - 60;
                legendOccupied.setBounds(40, ly, 180, 20);
                legendRegular.setBounds(40, ly+30, 180, 20);
                legendCompact.setBounds(40, ly+60, 180, 20);
                legendVIP.setBounds(40, ly+90, 180, 20);
                legendHandicap.setBounds(40, ly+120, 180, 20);
            }
        });
    }

    private void reloadGrid() {

        remove(topBlock);
        remove(bottomBlock);

        rowCounter = 1;

        topBlock = createRowBlock();
        bottomBlock = createRowBlock();

        add(topBlock);
        add(bottomBlock);

        revalidate();
        repaint();

        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
    }

    private JPanel createRowBlock(){
        RoundedPanel block = new RoundedPanel(40);
        block.setLayout(new GridLayout(2,1,0,0));
        block.setBackground(ThemeColors.PRIMARY);
        block.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        block.add(ParkingRowBuilder.createRow(
                currentFloor,
                rowCounter++,
                vipEnabled,
                handicapEnabled,
                null,
                selectedSpotHolder
        ));

        block.add(ParkingRowBuilder.createRow(
                currentFloor,
                rowCounter++,
                vipEnabled,
                handicapEnabled,
                null,
                selectedSpotHolder
        ));

        return block;
    }

    private JPanel createLegendBox(Color color,String text){
        JPanel p=new JPanel(null);
        p.setOpaque(false);

        JPanel box=new JPanel();
        box.setBackground(color);
        box.setBounds(0,2,15,15);

        JLabel l=new JLabel(text);
        l.setForeground(ThemeColors.SECONDARY);
        l.setBounds(25,0,150,20);

        p.add(box);
        p.add(l);
        return p;
    }
}
