import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class SpotSelectionUI extends JFrame {

    JPanel background;

    JPanel upFrom2nd, entry, exit, lift, up, down;
    JPanel topBlock, bottomBlock;

    JLabel downTopLeft, downLeftTopBlock, downLeftBottomBlock;
    JLabel leftTop, rightMiddle1, rightMiddle2;
    JLabel upRightTop, upRightBottom;

    JLabel arrowEntry, arrowExit, arrowUp, arrowDown;

    // TOP CONTROLS
    JButton returnBtn;
    JComboBox<String> floorDropdown;
    JPanel floorWrapper;

    // Legend panels
    JPanel legendOccupied, legendRegular, legendCompact, legendVIP, legendHandicap;

    boolean handicapEnabled = false;
    boolean vipEnabled = true;

    JPanel selectedSpot = null;
    private int rowCounter = 1;

    public SpotSelectionUI() {
        setTitle("Select Parking Spot");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        background = new JPanel(null);
        background.setBackground(new Color(10, 70, 100));
        add(background);

        // ROUNDED RETURN BUTTON
        returnBtn = new RoundedButton("← Return", new Color(255,90,90));
        background.add(returnBtn);

        // FLOOR DROPDOWN
        floorDropdown = new JComboBox<>(new String[]{
                "1st Floor",
                "2nd Floor",
                "3rd Floor"
        });
        floorDropdown.setFont(new Font("Arial", Font.BOLD, 14));
        floorDropdown.setOpaque(false);
        floorDropdown.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));

        floorWrapper = new RoundedPanel(30);
        floorWrapper.setBackground(new Color(40,190,210));
        floorWrapper.setLayout(new BorderLayout());
        floorWrapper.add(floorDropdown, BorderLayout.CENTER);
        background.add(floorWrapper);

        // RETURN ACTION
        returnBtn.addActionListener(e -> {
            new EntryPageUI().setVisible(true);
            dispose();
        });

        // FLOOR CHANGE → UPDATE VISUAL AIDS
        floorDropdown.addActionListener(e -> {
            String selected = (String) floorDropdown.getSelectedItem();

            if(selected.equals("1st Floor")) {
                updateFloorVisuals(1);
            }
            else if(selected.equals("2nd Floor")) {
                updateFloorVisuals(2);
            }
            else if(selected.equals("3rd Floor")) {
                updateFloorVisuals(3);
            }
        });

        topBlock = createRowBlock();
        bottomBlock = createRowBlock();
        background.add(topBlock);
        background.add(bottomBlock);

        legendOccupied = createLegendBox(Color.RED, "Occupied");
        legendRegular = createLegendBox(new Color(0,180,90), "Regular");
        legendCompact = createLegendBox(new Color(30,100,20), "Compact");
        legendVIP = createLegendBox(new Color(140,30,160), "VIP");
        legendHandicap = createLegendBox(new Color(70,160,220), "Handicapped");

        background.add(legendOccupied);
        background.add(legendRegular);
        background.add(legendCompact);
        background.add(legendVIP);
        background.add(legendHandicap);

        upFrom2nd = createSign("UP from 2nd Floor",170,70);
        entry = createSign("ENTRY",130,60);
        exit = createSign("EXIT",130,60);
        lift = createVerticalSign("LIFT",60,160);
        up = createSign("UP",130,70);
        down = createSign("DOWN",150,70);

        background.add(upFrom2nd);
        background.add(entry);
        background.add(exit);
        background.add(lift);
        background.add(up);
        background.add(down);

        downTopLeft = createArrow("↓",40);
        downLeftTopBlock = createArrow("↓",40);
        downLeftBottomBlock = createArrow("↓",40);

        leftTop = createArrow("←",45);
        rightMiddle1 = createArrow("→",45);
        rightMiddle2 = createArrow("→",45);

        upRightTop = createArrow("↑",40);
        upRightBottom = createArrow("↑",40);

        background.add(downTopLeft);
        background.add(downLeftTopBlock);
        background.add(downLeftBottomBlock);
        background.add(leftTop);
        background.add(rightMiddle1);
        background.add(rightMiddle2);
        background.add(upRightTop);
        background.add(upRightBottom);

        arrowEntry = createArrow("←",40);
        arrowExit = createArrow("→",40);
        arrowUp = createArrow("↑",40);
        arrowDown = createArrow("↓",40);

        background.add(arrowEntry);
        background.add(arrowExit);
        background.add(arrowUp);
        background.add(arrowDown);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {

                int panelW = background.getWidth();
                int panelH = background.getHeight();

                int blockW = 700;
                int blockH = 180;

                int topX = panelW/2 - blockW/2;
                int topY = panelH/2 - 220;

                int bottomX = topX;
                int bottomY = panelH/2 + 20;

                topBlock.setBounds(topX, topY, blockW, blockH);
                bottomBlock.setBounds(bottomX, bottomY, blockW, blockH);

                int ly = panelH/2 - 60;
                legendOccupied.setBounds(40, ly, 180, 20);
                legendRegular.setBounds(40, ly+30, 180, 20);
                legendCompact.setBounds(40, ly+60, 180, 20);
                legendVIP.setBounds(40, ly+90, 180, 20);
                legendHandicap.setBounds(40, ly+120, 180, 20);

                upFrom2nd.setBounds(0,0,170,70);
                entry.setBounds(panelW-125,0,130,60);
                exit.setBounds(panelW-125,65,130,60);
                lift.setBounds(panelW-70,panelH/2-80,60,160);
                up.setBounds(0,panelH-70,130,70);
                down.setBounds(panelW-150,panelH-70,150,70);

                returnBtn.setBounds(panelW/2 - 170, 10, 150, 45);
                floorWrapper.setBounds(panelW/2 - 5, 10, 180, 45);

                downTopLeft.setLocation(55, 75);
                downLeftTopBlock.setLocation(topX - 60, topY + 40);
                downLeftBottomBlock.setLocation(bottomX - 60, bottomY + 40);

                leftTop.setLocation(topX + blockW/2 - 20, topY - 70);

                rightMiddle1.setLocation(topX + blockW/2 - 20, topY + blockH + 10);
                rightMiddle2.setLocation(bottomX + blockW/2 - 20, bottomY + blockH + 10);

                upRightTop.setLocation(topX + blockW + 40, topY + 40);
                upRightBottom.setLocation(bottomX + blockW + 40, bottomY + 40);

                arrowEntry.setLocation(panelW - 200, 2);
                arrowExit.setLocation(panelW - 200, 65);
                arrowUp.setLocation(35, panelH - 135);
                arrowDown.setLocation(panelW - 105, panelH - 135);
            }
        });
    }

    // UPDATED FLOOR TEXT LOGIC
    private void updateFloorVisuals(int floor) {

        upFrom2nd.removeAll();
        entry.removeAll();

        JLabel leftLabel = null;
        JLabel rightLabel = null;

        if(floor == 1) {
            leftLabel = new JLabel("UP to 2nd");
            rightLabel = new JLabel("ENTRY");
        }
        else if(floor == 2) {
            leftLabel = new JLabel("UP from 3rd Floor");
            rightLabel = new JLabel("DOWN from 1st Floor");
        }
        else if(floor == 3) {
            rightLabel = new JLabel("DOWN from 2nd Floor");
        }

        if(leftLabel != null) {
            leftLabel.setFont(new Font("Arial", Font.BOLD, 14));
            upFrom2nd.add(leftLabel);
        }

        if(rightLabel != null) {
            rightLabel.setFont(new Font("Arial", Font.BOLD, 14));
            entry.add(rightLabel);
        }

        upFrom2nd.revalidate();
        upFrom2nd.repaint();

        entry.revalidate();
        entry.repaint();
    }

    private JPanel createRowBlock(){
        RoundedPanel block = new RoundedPanel(40);
        block.setLayout(new GridLayout(2,1,0,0));
        block.setBackground(new Color(20,90,60));
        block.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        block.add(createSingleRow(rowCounter++));
        block.add(createSingleRow(rowCounter++));
        return block;
    }

    private JPanel createSingleRow(int rowIndex){
        JPanel legendRow = new JPanel(new GridLayout(1,10,0,0));
        legendRow.setOpaque(false);

        for(int i=0;i<10;i++){
            Color c;
            if(i<3) c=new Color(30,100,20);
            else if(i<6) c=new Color(0,180,90);
            else if(i<8) c=new Color(140,30,160);
            else c=new Color(70,160,220);

            JPanel spot=new JPanel();
            spot.setBackground(c);
            spot.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));
            legendRow.add(spot);
        }
        return legendRow;
    }

    private JPanel createLegendBox(Color color,String text){
        JPanel p=new JPanel(null);
        p.setOpaque(false);
        JPanel box=new JPanel();
        box.setBackground(color);
        box.setBounds(0,2,15,15);
        JLabel l=new JLabel(text);
        l.setForeground(Color.WHITE);
        l.setBounds(25,0,150,20);
        p.add(box);
        p.add(l);
        return p;
    }

    private JPanel createSign(String text,int w,int h){
        JPanel p=new JPanel();
        p.setBackground(new Color(255,190,0));
        p.setSize(w,h);
        p.setLayout(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }

    private JPanel createVerticalSign(String text,int w,int h){
        JPanel p=new JPanel();
        p.setBackground(new Color(255,190,0));
        p.setSize(w,h);
        p.setLayout(new GridBagLayout());
        p.add(new JLabel("<html>"+text.replace("", "<br>").trim()+"</html>"));
        return p;
    }

    private JLabel createArrow(String symbol,int size){
        JLabel arrow=new JLabel(symbol,SwingConstants.CENTER);
        arrow.setForeground(new Color(255,210,60));
        arrow.setFont(new Font("Arial",Font.BOLD,size));
        arrow.setSize(size+20,size+20);
        return arrow;
    }

    class RoundedPanel extends JPanel{
        int r;
        RoundedPanel(int r){this.r=r;setOpaque(false);}
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
            super.paintComponent(g);
        }
    }

    class RoundedButton extends JButton {
        Color color;
        RoundedButton(String text, Color c){
            super(text);
            color=c;
            setForeground(Color.BLACK);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFont(new Font("Arial", Font.BOLD, 14));
        }
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
            super.paintComponent(g);
        }
    }

    // Test
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new SpotSelectionUI().setVisible(true));
    }
}
