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
        background.setBackground(ThemeColors.PRIMARY);
        add(background);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(ThemeColors.SECONDARY); 
        card.setPreferredSize(new Dimension(760,620));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(30,40,30,40));

        card.add(createCenter("Receipt", 20, true));
        card.add(Box.createVerticalStrut(20));

        String plate = (receipt != null) ? receipt.getLicensePlate() : "No Record Found";
        String dateStr = (receipt != null) ? receipt.getExitTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "-";

        RoundedPanel infoBox = createSection();
        infoBox.add(createCenter("License Plate: " + plate, 14, false));
        infoBox.add(createCenter("Date: " + dateStr, 14, false));

        String entryStr = (receipt != null) ? receipt.getEntryTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "-";
        String exitStr = (receipt != null) ? receipt.getExitTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : "-";
        String durationStr = (receipt != null) ? String.format("%.1f hours", receipt.getHoursParked()) : "-";

        infoBox.add(Box.createVerticalStrut(10));
        infoBox.add(createCenter("Entry Time: " + entryStr, 14, false));
        infoBox.add(createCenter("Exit Time: " + exitStr, 14, false));
        infoBox.add(createCenter("Duration: " + durationStr, 14, false));

        card.add(infoBox);
        card.add(Box.createVerticalStrut(18));

        RoundedPanel feeBox = createSection();
        feeBox.add(createCenter("Parking Fee Breakdown", 15, true));
        feeBox.add(Box.createVerticalStrut(10));

        String feeStr = (receipt != null) ? String.format("RM %.2f", receipt.getParkingFee()) : "-";
        String finesPaidStr = (receipt != null) ? String.format("RM %.2f", receipt.getFines()) : "-"; 

        feeBox.add(createCenter("Parking Fee: " + feeStr, 14, false));
        feeBox.add(createCenter("Fines Paid: " + finesPaidStr, 14, false));

        card.add(feeBox);
        card.add(Box.createVerticalStrut(18));

        RoundedPanel paymentBox = createSection();

        String method = (receipt != null) ? receipt.getPaymentMethod() : "-";
        String totalPaidStr = (receipt != null) ? String.format("RM %.2f", receipt.getAmountPaid()) : "-";

        paymentBox.add(createCenter("Payment Method: " + method, 14, false));
        paymentBox.add(createCenter("Total Paid: " + totalPaidStr, 14, false));

        paymentBox.add(Box.createVerticalStrut(10));

        String outstandingStr = (receipt != null) ? String.format("RM %.2f", receipt.getTotalFinesOutstanding()) : "-";
        paymentBox.add(createCenter("Outstanding Fines: " + outstandingStr, 14, false));

        if (receipt != null && receipt.getChange() > 0) {
            paymentBox.add(createCenter("Change: " + String.format("RM %.2f", receipt.getChange()), 14, false));
        }

        card.add(paymentBox);
        card.add(Box.createVerticalStrut(25));

        card.add(createCenter("Thank you for using our parking facility.", 14, false));
        card.add(Box.createVerticalStrut(25));

        RoundedButton closeBtn = new RoundedButton("Close", ThemeColors.PRIMARY);
        closeBtn.setForeground(ThemeColors.SECONDARY);
        closeBtn.setMaximumSize(new Dimension(160,45));
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        closeBtn.addActionListener(e -> {
            new MainMenuUI().setVisible(true);
            dispose();
        });

        card.add(closeBtn);
        background.add(card);
    }

    private RoundedPanel createSection(){
        RoundedPanel section = new RoundedPanel(20);
        section.setBackground(ThemeColors.PRIMARY);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(new EmptyBorder(15,20,15,20));
        section.setAlignmentX(Component.CENTER_ALIGNMENT);
        section.setMaximumSize(new Dimension(520, 180));
        return section;
    }

    private JLabel createCenter(String text, int size, boolean bold){
        JLabel l = new JLabel(text);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setForeground(ThemeColors.SECONDARY);
        l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        return l;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            new ReceiptPageUI("ABC1234").setVisible(true);
        });
    }
}