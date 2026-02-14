package UserInterface;

import ExitModule.ExitSystem;
import ExitModule.ExitSystem.PendingExit;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ExitPageUI extends JFrame {

    private JLabel lblEntry, lblExit, lblDuration, lblSpot, lblVehicle;
    private JLabel lblFee, lblFine, lblUnpaid, lblTotal;
    private JTextField plateField;
    
    private PendingExit currentExitData;

    public ExitPageUI() {

        setTitle("Parking Exit");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63)); // #02343F
        add(background);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,0,10,0);

        RoundedPanel titleCard = new RoundedPanel(25);
        titleCard.setBackground(new Color(240,237,204)); // #F0EDCC
        titleCard.setPreferredSize(new Dimension(360,80));
        titleCard.setLayout(new GridLayout(2,1));

        JLabel title = new JLabel("Parking Exit", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 15));
        title.setForeground(new Color(2,52,63));

        JLabel subtitle = new JLabel("Retrieve Vehicle Details and Complete Payment", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(new Color(2,52,63));

        titleCard.add(title);
        titleCard.add(subtitle);

        gbc.gridy = 0;
        background.add(titleCard, gbc);

        JLabel plateLabel = new JLabel("Number Plate");
        plateLabel.setForeground(new Color(240,237,204));
        gbc.gridy = 1;
        background.add(plateLabel, gbc);

        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        inputRow.setOpaque(false);

        RoundedPanel plateFieldWrapper = new RoundedPanel(20);
        plateFieldWrapper.setBackground(new Color(240,237,204));
        plateFieldWrapper.setPreferredSize(new Dimension(260,50));
        plateFieldWrapper.setLayout(new BorderLayout());

        plateField = new JTextField("ABC1234"); // Default for testing
        plateField.setBorder(new EmptyBorder(10,15,10,15));
        plateField.setOpaque(false);
        plateField.setForeground(new Color(2,52,63));
        plateFieldWrapper.add(plateField);

        RoundedButton findBtn = new RoundedButton("FIND", new Color(240,237,204));
        findBtn.setPreferredSize(new Dimension(90,50));

        findBtn.addActionListener(e -> performSearch());

        inputRow.add(plateFieldWrapper);
        inputRow.add(findBtn);

        gbc.gridy = 2;
        background.add(inputRow, gbc);

        RoundedPanel summaryCard = new RoundedPanel(25);
        summaryCard.setBackground(new Color(240,237,204));
        summaryCard.setPreferredSize(new Dimension(400,300));
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBorder(new EmptyBorder(15,20,15,20));

        summaryCard.add(createText("Parking Summary"));
        summaryCard.add(Box.createVerticalStrut(10));

        lblEntry = createText("Entry Time: -");
        lblExit = createText("Exit Time: -");
        lblDuration = createText("Hours Parked: -");
        lblSpot = createText("Spot Type: -");
        lblVehicle = createText("Vehicle Type: -");

        summaryCard.add(lblEntry);
        summaryCard.add(lblExit);
        summaryCard.add(lblDuration);
        summaryCard.add(lblSpot);
        summaryCard.add(lblVehicle);

        summaryCard.add(Box.createVerticalStrut(15));

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(2,52,63));
        sep1.setBackground(new Color(2,52,63));
        summaryCard.add(sep1);

        summaryCard.add(Box.createVerticalStrut(10));
        
        lblFee = createText("Parking Fee: -");
        lblFine = createText("Current Fine: -");
        lblUnpaid = createText("Unpaid Fines: -");

        summaryCard.add(lblFee);
        summaryCard.add(lblFine);
        summaryCard.add(lblUnpaid);

        summaryCard.add(Box.createVerticalStrut(10));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(2,52,63));
        sep2.setBackground(new Color(2,52,63));
        summaryCard.add(sep2);

        summaryCard.add(Box.createVerticalStrut(10));
        
        lblTotal = createText("Total Due: -");
        summaryCard.add(lblTotal);

        gbc.gridy = 3;
        background.add(summaryCard, gbc);

        RoundedButton payBtn = new RoundedButton("Pay Now", new Color(240,237,204));
        payBtn.setPreferredSize(new Dimension(150,50));

        payBtn.addActionListener(e -> {
            if (currentExitData == null) {
                JOptionPane.showMessageDialog(this, "Please search for a vehicle first.");
                return;
            }
            new PaymentPageUI(currentExitData).setVisible(true);
            dispose();
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(25,0,10,0);
        background.add(payBtn, gbc);
    }

    private JLabel createText(String text){
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(2,52,63));
        return l;
    }

    private void performSearch() {
        String plate = plateField.getText().trim();
        if (plate.isEmpty() || plate.contains("Placeholder")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid License Plate.");
            return;
        }

        ExitSystem exitSystem = new ExitSystem();
        currentExitData = exitSystem.initiateExit(plate);

        if (currentExitData == null) {
            JOptionPane.showMessageDialog(this, "No active ticket found for plate: " + plate, "Search Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        lblEntry.setText("Entry Time: " + currentExitData.getEntryTime().format(timeFmt));
        lblExit.setText("Exit Time: " + currentExitData.getInitiatedTime().format(timeFmt));
        
        long durationMin = java.time.Duration.between(currentExitData.getEntryTime(), currentExitData.getInitiatedTime()).toMinutes();
        double durationHrs = Math.ceil(durationMin / 60.0);
        if(durationMin <= 0) durationHrs = 1.0;
        
        lblDuration.setText(String.format("Hours Parked: %.0f hrs", durationHrs));
        lblSpot.setText("Spot Type: " + currentExitData.getTicket().getSpotType());
        lblVehicle.setText("Vehicle Type: " + currentExitData.getTicket().getVehicleType());
        
        lblFee.setText(String.format("Parking Fee: RM %.2f", currentExitData.getParkingFee()));
        lblFine.setText(String.format("Current Fine: RM %.2f", currentExitData.getCurrentFines()));
        lblUnpaid.setText(String.format("Unpaid Fines: RM %.2f", currentExitData.getUnpaidFines()));
        
        lblTotal.setText(String.format("Total Due: RM %.2f", currentExitData.getTotalDue()));
        
        JOptionPane.showMessageDialog(this, "Vehicle Found! Review summary below.");
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new ExitPageUI().setVisible(true));
    }
}