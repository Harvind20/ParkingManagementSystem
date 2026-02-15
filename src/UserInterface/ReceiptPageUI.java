package UserInterface;

import ExitModule.Receipt;
import coreParkingSystem.ReceiptDAO;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ReceiptPageUI extends JFrame {

    public ReceiptPageUI() {
        this((Receipt)null); 
    }

    public ReceiptPageUI(String licensePlate) {
        this(new ReceiptDAO().getLatestReceipt(licensePlate));
        if (licensePlate != null && this.getTitle().equals("Receipt")) {
        }
    }

    public ReceiptPageUI(Receipt receipt) {

        setTitle("Receipt");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63));
        add(background);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(new Color(240,237,204)); 
        card.setPreferredSize(new Dimension(720,600));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30,40,30,40));

        card.add(Box.createVerticalStrut(35));

        JLabel title = createCenter("Receipt", 18, true);
        card.add(title);

        card.add(Box.createVerticalStrut(25));

        String plate = (receipt != null) ? receipt.getLicensePlate() : "No Record Found";
        String dateStr = (receipt != null) ? receipt.getExitTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-";
        
        card.add(createCenter("License Plate: " + plate, 14, false));
        card.add(createCenter("Date: " + dateStr, 14, false));

        card.add(Box.createVerticalStrut(18));

        String entryStr = (receipt != null) ? receipt.getEntryTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "-";
        String exitStr = (receipt != null) ? receipt.getExitTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "-";
        String durationStr = (receipt != null) ? String.format("%.1f hours", receipt.getHoursParked()) : "-";

        card.add(createCenter("Entry Time: " + entryStr, 14, false));
        card.add(createCenter("Exit Time: " + exitStr, 14, false));
        card.add(createCenter("Duration: " + durationStr, 14, false));

        card.add(Box.createVerticalStrut(20));

        card.add(createCenter("Parking Fee Breakdown", 15, true));
        card.add(Box.createVerticalStrut(10));

        String feeStr = (receipt != null) ? String.format("RM %.2f", receipt.getParkingFee()) : "-";
        String finesPaidStr = (receipt != null) ? String.format("RM %.2f", receipt.getFines()) : "-"; 
        
        card.add(createCenter("Parking Fee: " + feeStr, 14, false));
        card.add(createCenter("Fines Paid: " + finesPaidStr, 14, false));
        
        card.add(Box.createVerticalStrut(20));

        String method = (receipt != null) ? receipt.getPaymentMethod() : "-";
        String totalPaidStr = (receipt != null) ? String.format("RM %.2f", receipt.getAmountPaid()) : "-";

        card.add(createCenter("Payment Method: " + method, 14, false));
        card.add(createCenter("Total Paid: " + totalPaidStr, 14, false));

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
        SwingUtilities.invokeLater(() -> {
            new ReceiptPageUI("ABC1234").setVisible(true);
        });
    }
}