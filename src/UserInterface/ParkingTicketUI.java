import javax.swing.*;
import java.awt.*;

public class ParkingTicketUI extends JFrame {

    public ParkingTicketUI() {

        setTitle("Parking Ticket");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63)); // #02343F
        add(background);

        RoundedPanel ticket = new RoundedPanel(30);
        ticket.setBackground(new Color(240,237,204)); // #F0EDCC
        ticket.setPreferredSize(new Dimension(260, 300));
        ticket.setLayout(new BoxLayout(ticket, BoxLayout.Y_AXIS));
        ticket.setBorder(BorderFactory.createEmptyBorder(25,20,25,20));

        JLabel title = new JLabel("Parking Ticket");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(2,52,63));
        ticket.add(title);

        ticket.add(Box.createVerticalStrut(12));

        ticket.add(createLabel("Ticket ID:"));

        ticket.add(Box.createVerticalStrut(12));

        ticket.add(createLabel("License Plate:"));
        ticket.add(createLabel("Spot Location:"));
        ticket.add(createLabel("Floor:"));

        ticket.add(Box.createVerticalStrut(12));

        ticket.add(createLabel("Entry Time:"));
        ticket.add(createLabel("Date:"));

        ticket.add(Box.createVerticalGlue());

        RoundedButton okBtn = new RoundedButton("OK", new Color(120,200,80));
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.setMaximumSize(new Dimension(120,40));

        // Redirect to MainMenu
        okBtn.addActionListener(e -> {
            new MainMenuUI().setVisible(true);
            dispose();
        });

        ticket.add(okBtn);

        background.add(ticket);
    }

    private JLabel createLabel(String text){
        JLabel l = new JLabel(text);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(2,52,63));
        return l;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new ParkingTicketUI().setVisible(true));
    }
}
