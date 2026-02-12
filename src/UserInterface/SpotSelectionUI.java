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

    boolean vipEnabled = false;
    boolean reservedEnabled = false;

    JPanel selectedSpot = null;

    // Row counter to label rows correctly
    private int rowCounter = 1;

    public SpotSelectionUI() {
        setTitle("Select Parking Spot");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        background = new JPanel(null);
        background.setBackground(new Color(10, 70, 100));
        add(background);

        topBlock = createRowBlock();
        bottomBlock = createRowBlock();
        background.add(topBlock);
        background.add(bottomBlock);

        background.add(createLegendBox(Color.RED, "Occupied"));
        background.add(createLegendBox(new Color(0,180,90), "Regular"));
        background.add(createLegendBox(new Color(30,100,20), "Compact"));
        background.add(createLegendBox(new Color(140,30,160), "Reserved"));
        background.add(createLegendBox(new Color(70,160,220), "Handicapped"));

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
                for(int i=2;i<=6;i++){
                    background.getComponent(i).setBounds(40, ly + (i-2)*30,180,20);
                }

                upFrom2nd.setBounds(0,0,170,70);
                entry.setBounds(panelW-125,0,130,60);
                exit.setBounds(panelW-125,65,130,60);
                lift.setBounds(panelW-70,panelH/2-80,60,160);
                up.setBounds(0,panelH-70,130,70);
                down.setBounds(panelW-150,panelH-70,150,70);

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
            String type;

            if(i<3){ c=new Color(30,100,20); type="COMPACT"; }
            else if(i<6){ c=new Color(0,180,90); type="REGULAR"; }
            else if(i<8){ c=new Color(140,30,160); type="RESERVED"; }
            else{ c=new Color(70,160,220); type="VIP"; }

            JPanel spot=new JPanel();
            spot.setBackground(c);
            spot.setBorder(BorderFactory.createLineBorder(Color.WHITE,1));

            if(type.equals("VIP") && !vipEnabled){
                spot.setEnabled(false);
            }

            if(type.equals("RESERVED") && !reservedEnabled){
                spot.setEnabled(false);
            }

            int spotNumber = i + 1;

            spot.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {

                    if(!spot.isEnabled()) return;

                    if(selectedSpot != null){
                        selectedSpot.setBorder(
                            BorderFactory.createLineBorder(Color.WHITE,1)
                        );
                    }

                    spot.setBorder(BorderFactory.createLineBorder(Color.YELLOW,3));
                    selectedSpot = spot;

                    new ConfirmSpotDialog(
                        SpotSelectionUI.this,
                        "Row " + rowIndex + " Spot " + spotNumber
                    ).setVisible(true);
                }
            });

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
        JLabel l=new JLabel(text);
        l.setFont(new Font("Arial",Font.BOLD,14));
        p.add(l);
        return p;
    }

    private JPanel createVerticalSign(String text,int w,int h){
        JPanel p=new JPanel();
        p.setBackground(new Color(255,190,0));
        p.setSize(w,h);
        p.setLayout(new GridBagLayout());
        String vertical="<html>"+text.replace("", "<br>").trim()+"</html>";
        p.add(new JLabel(vertical));
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
            g2.setColor(getBackground());
            g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
            super.paintComponent(g);
        }
    }

    // Test
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new SpotSelectionUI().setVisible(true));
    }
}
