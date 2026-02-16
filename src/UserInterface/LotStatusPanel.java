package UserInterface;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.*;

public class LotStatusPanel extends JPanel {

    private JPanel topBlock, bottomBlock;
    private JComboBox<String> floorDropdown;
    private JPanel floorWrapper; 

    private JPanel legendOccupied, legendRegular, legendCompact, legendVIP, legendHandicap;

    private int rowCounter = 1;
    private int currentFloor = 1;

    boolean handicapEnabled = true;
    boolean vipEnabled = true;

    // holds currently selected spot (shared reference)
    JPanel[] selectedSpotHolder = new JPanel[1];
    
    // timer to auto-refresh the grid periodically
    private Timer refreshTimer;

    public LotStatusPanel() {

        setLayout(null);
        setBackground(ThemeColors.PRIMARY);

        // return button back to main menu
        RoundedButton returnBtn = new RoundedButton("Return", ThemeColors.SECONDARY);
        returnBtn.setForeground(ThemeColors.PRIMARY);
        returnBtn.setBounds(30, 20, 120, 40);
        add(returnBtn);

        returnBtn.addActionListener(e -> {
            if (refreshTimer != null) refreshTimer.stop(); // stop auto refresh
            new MainMenuUI().setVisible(true);
            SwingUtilities.getWindowAncestor(this).dispose();
        });

        // floor selection dropdown
        floorDropdown = new JComboBox<>(new String[]{
                "1st Floor",
                "2nd Floor",
                "3rd Floor"
        });

        floorWrapper = new RoundedPanel(25);
        floorWrapper.setBackground(ThemeColors.SECONDARY);
        floorWrapper.setLayout(new BorderLayout());
        floorWrapper.add(floorDropdown);
        add(floorWrapper);

        // reload grid when floor changes
        floorDropdown.addActionListener(e -> {
            String selected = (String) floorDropdown.getSelectedItem();

            if(selected.equals("1st Floor")) currentFloor = 1;
            else if(selected.equals("2nd Floor")) currentFloor = 2;
            else currentFloor = 3;

            reloadGrid();
        });

        // initial creation of parking rows
        topBlock = createRowBlock();
        bottomBlock = createRowBlock();

        add(topBlock);
        add(bottomBlock);

        // legend explaining color meanings
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

        // reposition UI elements when window is resized
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateLayout();
            }
        });

        // auto refresh every 5 seconds to keep spot status updated
        refreshTimer = new Timer(5000, e -> reloadGrid());
        refreshTimer.start();
    }

    // recalculates positions for blocks and legends based on panel size
    private void updateLayout() {
        int panelW = getWidth();
        int panelH = getHeight();

        if (panelW == 0 || panelH == 0) return;

        int blockW = 700;
        int blockH = 180;

        int topX = panelW/2 - blockW/2;
        int topY = panelH/2 - 220;

        int bottomX = topX;
        int bottomY = panelH/2 + 20;

        topBlock.setBounds(topX, topY, blockW, blockH);
        bottomBlock.setBounds(bottomX, bottomY, blockW, blockH);

        // center the floor dropdown at top
        floorWrapper.setBounds(panelW/2 - 100, 20, 200, 40);

        int ly = panelH/2 - 60;
        legendOccupied.setBounds(40, ly, 180, 20);
        legendRegular.setBounds(40, ly+30, 180, 20);
        legendCompact.setBounds(40, ly+60, 180, 20);
        legendVIP.setBounds(40, ly+90, 180, 20);
        legendHandicap.setBounds(40, ly+120, 180, 20);
    }

    // rebuilds the parking rows (used when floor changes or auto-refresh triggers)
    private void reloadGrid() {
        remove(topBlock);
        remove(bottomBlock);

        rowCounter = 1; // reset row numbering for selected floor

        topBlock = createRowBlock();
        bottomBlock = createRowBlock();

        add(topBlock);
        add(bottomBlock);

        updateLayout();
        revalidate();
        repaint();
    }

    // creates a 2-row block of parking spots using ParkingRowBuilder
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

    // small colored legend box with label text
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
