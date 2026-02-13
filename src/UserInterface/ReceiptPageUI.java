package UserInterface;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ReceiptPageUI extends JFrame {

    public ReceiptPageUI() {

        setTitle("Receipt");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63)); // #02343F
        add(background);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(new Color(240,237,204)); // #F0EDCC
        card.setPreferredSize(new Dimension(720,600));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30,40,30,40));

        card.add(Box.createVerticalStrut(35));

        JLabel title = createCenter("Receipt", 18, true);
        card.add(title);

        card.add(Box.createVerticalStrut(25));

        card.add(createCenter("License Plate:", 14, false));
        card.add(createCenter("Date:", 14, false));

        card.add(Box.createVerticalStrut(18));

        card.add(createCenter("Entry Time:", 14, false));
        card.add(createCenter("Exit Time:", 14, false));
        card.add(createCenter("Duration:", 14, false));

        card.add(Box.createVerticalStrut(20));

        card.add(createCenter("Parking Fee Breakdown", 15, true));
        card.add(Box.createVerticalStrut(10));

        card.add(createCenter("Hourly Rate:", 14, false));
        card.add(createCenter("Parking Fee:", 14, false));
        card.add(createCenter("Current Fine:", 14, false));
        card.add(createCenter("Outstanding Fine:", 14, false));
        card.add(createCenter("Total:", 14, false));

        card.add(Box.createVerticalStrut(20));

        card.add(createCenter("Payment Method:", 14, false));
        card.add(createCenter("Total Paid:", 14, false));
        card.add(createCenter("Balance:", 14, false));
        card.add(createCenter("New Outstanding Fine:", 14, false));

        card.add(Box.createVerticalStrut(28));

        card.add(createCenter("Thank you for using our parking facility.", 14, false));

        card.add(Box.createVerticalStrut(25));

        RoundedButton closeBtn = new RoundedButton("Close", new Color(2,52,63));
        closeBtn.setForeground(new Color(240,237,204));
        closeBtn.setMaximumSize(new Dimension(160,45));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        closeBtn.addActionListener(e -> {
            new MainMenuUI().setVisible(true);
            dispose();
        });

        card.add(closeBtn);

        background.add(card);
    }

    private JLabel createCenter(String text, int size, boolean bold){
        JLabel l = new JLabel(text);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setForeground(new Color(2,52,63));
        l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        return l;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new ReceiptPageUI().setVisible(true));
    }
}
