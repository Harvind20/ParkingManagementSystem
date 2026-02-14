package UserInterface;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

public class ParkingTicketUI extends JFrame {

    public ParkingTicketUI(String fullSuccessMsg, String plate) {

        setTitle("Parking Ticket");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63)); 
        add(background);

        RoundedPanel ticket = new RoundedPanel(30);
        ticket.setBackground(new Color(240,237,204)); 
        ticket.setPreferredSize(new Dimension(300, 350)); 
        ticket.setLayout(new BoxLayout(ticket, BoxLayout.Y_AXIS));
        ticket.setBorder(BorderFactory.createEmptyBorder(25,20,25,20));

        JLabel title = new JLabel("Parking Ticket");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(2,52,63));
        ticket.add(title);

        ticket.add(Box.createVerticalStrut(20));

        String cleanTicketID = "Unknown";
        try {
            if(fullSuccessMsg.contains("Ticket ID: ")) {
                int start = fullSuccessMsg.indexOf("Ticket ID: ") + 11;
                int end = fullSuccessMsg.indexOf(" |", start);
                if(end == -1) end = fullSuccessMsg.length();
                cleanTicketID = fullSuccessMsg.substring(start, end);
            } else {
                cleanTicketID = fullSuccessMsg.replace("SUCCESS: ", "");
            }
        } catch(Exception e) {
            cleanTicketID = "Error Parsing ID";
        }
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        // --- DISPLAY LABELS ---
        ticket.add(createLabel("Ticket ID: " + cleanTicketID));
        ticket.add(Box.createVerticalStrut(10));
        ticket.add(createLabel("License Plate: " + plate));
        ticket.add(Box.createVerticalStrut(10));
        ticket.add(createLabel("Entry Time: " + now.format(timeFmt)));
        ticket.add(createLabel("Date: " + now.format(dateFmt)));

        ticket.add(Box.createVerticalGlue());

        RoundedButton okBtn = new RoundedButton("OK", new Color(120,200,80));
        okBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        okBtn.setMaximumSize(new Dimension(120,40));

        okBtn.addActionListener(e -> {
            new SpotSelectionUI().setVisible(true);
            dispose();
        });

        ticket.add(okBtn);
        background.add(ticket);
    }

    private JLabel createLabel(String text){
        JLabel l = new JLabel(text);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        l.setForeground(new Color(2,52,63));
        return l;
    }
}