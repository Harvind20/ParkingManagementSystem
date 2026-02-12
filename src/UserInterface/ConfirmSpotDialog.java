import javax.swing.*;
import java.awt.*;

public class ConfirmSpotDialog extends JDialog {

    public ConfirmSpotDialog(JFrame parent, String spotId) {
        super(parent, true);
        setSize(300, 360);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0)); // transparent outside

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        add(wrapper);

        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(new Color(160,160,160));
        card.setPreferredSize(new Dimension(230,300));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));

        JLabel t1 = createLabel("You");
        JLabel t2 = createLabel("are about to");
        JLabel t3 = createLabel("park at");

        JLabel spot = new JLabel(spotId);
        spot.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.setFont(new Font("Arial",Font.BOLD,16));
        spot.setForeground(Color.BLACK);

        JButton cancel = new RoundedButton("Cancel", Color.RED);
        JButton confirm = new RoundedButton("Confirm", new Color(120,200,80));

        cancel.addActionListener(e -> dispose());
        confirm.addActionListener(e -> {
            JOptionPane.showMessageDialog(parent,
                    "You parked at " + spotId);
            dispose();
        });

        card.add(Box.createVerticalStrut(35));
        card.add(t1);
        card.add(t2);
        card.add(t3);
        card.add(Box.createVerticalStrut(10));
        card.add(spot);
        card.add(Box.createVerticalStrut(30));
        card.add(cancel);
        card.add(Box.createVerticalStrut(12));
        card.add(confirm);

        wrapper.add(card);
    }

    private JLabel createLabel(String text){
        JLabel l=new JLabel(text);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setForeground(Color.WHITE);
        return l;
    }

    // Rounded card
    class RoundedPanel extends JPanel {
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

    // Rounded buttons
    class RoundedButton extends JButton {
        Color color;
        RoundedButton(String text, Color c){
            super(text);
            color=c;
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setMaximumSize(new Dimension(140,38));
        }
        protected void paintComponent(Graphics g){
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0,0,getWidth(),getHeight(),25,25);
            super.paintComponent(g);
        }
    }
}
