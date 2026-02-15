package UserInterface;

import ExitModule.ExitSystem;
import ExitModule.ExitSystem.PendingExit;
import ExitModule.Receipt;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PaymentPageUI extends JFrame {

    private PendingExit pendingExit;
    private double selectedTotal = 0.0;
    private double fineAmountToPay = 0.0;
    private double parkingFee = 0.0;
    private double totalFines = 0.0;

    private JLabel feeOnlyAmount;
    private JLabel fullTotal;
    private JLabel partialTotal;
    private JRadioButton full;

    public PaymentPageUI() {
        this(null);
    }

    public PaymentPageUI(PendingExit exitData) {
        this.pendingExit = exitData;

        setTitle("Payment");
        setSize(900,720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63));
        add(background);

        RoundedPanel mainCard = new RoundedPanel(30);
        mainCard.setBackground(new Color(240,237,204));
        mainCard.setPreferredSize(new Dimension(780,660));
        mainCard.setLayout(new BoxLayout(mainCard, BoxLayout.Y_AXIS));
        mainCard.setBorder(new EmptyBorder(25,40,25,40));
        background.add(mainCard);

        RoundedPanel methodBox = createBox();
        methodBox.add(centerHeader("Payment Method"));

        String[] methods = {"Cash", "Card"};
        JComboBox<String> methodDropdown = new JComboBox<>(methods);
        methodDropdown.setMaximumSize(new Dimension(180,32));
        methodDropdown.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel cashPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cashPanel.setOpaque(false);
        JLabel cashLabel = new JLabel("Enter Cash Amount: RM ");
        cashLabel.setForeground(new Color(2,52,63));
        JTextField cashInput = new JTextField(8);
        cashPanel.add(cashLabel);
        cashPanel.add(cashInput);

        methodDropdown.addActionListener(e -> {
            String selected = (String) methodDropdown.getSelectedItem();
            cashPanel.setVisible(selected.equals("Cash"));
        });

        methodBox.add(centerRow(methodDropdown));
        methodBox.add(Box.createVerticalStrut(10));
        methodBox.add(cashPanel);

        mainCard.add(methodBox);
        mainCard.add(Box.createVerticalStrut(15));

        parkingFee = (pendingExit != null) ? pendingExit.getParkingFee() : 5.0;
        totalFines = (pendingExit != null) ? pendingExit.getCurrentFines() + pendingExit.getUnpaidFines() : 50.0;
        double totalAll = parkingFee + totalFines;

        selectedTotal = totalAll;
        fineAmountToPay = totalFines;

        RoundedPanel feeBox = createBox();

        JRadioButton feeOnly = new JRadioButton("Pay Ticket Fee Only");
        styleRadio(feeOnly);
        feeOnlyAmount = centerText("RM " + String.format("%.2f", parkingFee));

        feeBox.add(centerRow(feeOnly));
        feeBox.add(feeOnlyAmount);

        mainCard.add(feeBox);
        mainCard.add(Box.createVerticalStrut(12));

        RoundedPanel partialBox = createBox();

        JRadioButton partial = new JRadioButton("Ticket Fee + Partial Fine Payment");
        styleRadio(partial);

        JPanel partialRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        partialRow.setOpaque(false);
        JLabel partialLabel = new JLabel("Amount to pay towards fines: RM ");
        partialLabel.setForeground(new Color(2,52,63));
        JTextField partialInput = new JTextField(8);
        partialRow.add(partialLabel);
        partialRow.add(partialInput);

        partialTotal = centerText("Total: RM -");
        partialInput.setEnabled(false);

        partialBox.add(centerRow(partial));
        partialBox.add(partialRow);
        partialBox.add(partialTotal);

        mainCard.add(partialBox);
        mainCard.add(Box.createVerticalStrut(12));

        RoundedPanel fullBox = createBox();

        full = new JRadioButton("Ticket Fee + Pay All Outstanding Fines");
        styleRadio(full);
        fullTotal = centerText("Total: RM " + String.format("%.2f", totalAll));
        full.setSelected(true);

        fullBox.add(centerRow(full));
        fullBox.add(fullTotal);

        mainCard.add(fullBox);
        mainCard.add(Box.createVerticalStrut(20));

        ButtonGroup amountGroup = new ButtonGroup();
        amountGroup.add(feeOnly);
        amountGroup.add(partial);
        amountGroup.add(full);

        feeOnly.addActionListener(e -> {
            selectedTotal = parkingFee;
            fineAmountToPay = 0.0;
            partialInput.setEnabled(false);
            partialInput.setText("");
            partialTotal.setText("Total: RM -");
        });

        full.addActionListener(e -> {
            selectedTotal = parkingFee + totalFines;
            fineAmountToPay = totalFines;
            partialInput.setEnabled(false);
            partialInput.setText("");
            partialTotal.setText("Total: RM -");
        });

        partial.addActionListener(e -> {
            partialInput.setEnabled(true);
            selectedTotal = parkingFee;
            fineAmountToPay = 0.0;
            partialTotal.setText("Total: RM " + String.format("%.2f", parkingFee));
        });

        partialInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (partial.isSelected()) {
                    try {
                        String text = partialInput.getText();
                        if(text.isEmpty()) {
                            selectedTotal = parkingFee;
                            fineAmountToPay = 0.0;
                            partialTotal.setText("Total: RM " + String.format("%.2f", selectedTotal));
                            return;
                        }
                        double pFine = Double.parseDouble(text);
                        if (pFine > totalFines) pFine = totalFines;
                        fineAmountToPay = pFine;
                        selectedTotal = parkingFee + pFine;
                        partialTotal.setText("Total: RM " + String.format("%.2f", selectedTotal));
                    } catch (NumberFormatException e) {
                        partialTotal.setText("Total: Invalid Input");
                    }
                }
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER,25,0));
        btnRow.setOpaque(false);

        RoundedButton confirm = new RoundedButton("Confirm", new Color(120,200,80));
        RoundedButton cancel = new RoundedButton("Cancel", new Color(255,60,60));
        confirm.setPreferredSize(new Dimension(150,48));
        cancel.setPreferredSize(new Dimension(150,48));

        confirm.addActionListener(e -> {
            if (pendingExit == null) return;

            double oldFee = pendingExit.getParkingFee();
            double oldFines = pendingExit.getCurrentFines();

            if (methodDropdown.getSelectedItem().equals("Cash")) {
                try {
                    String cashText = cashInput.getText().trim();
                    if(cashText.isEmpty()) throw new NumberFormatException();
                    double cashGiven = Double.parseDouble(cashText);
                    if (cashGiven < selectedTotal) {
                        JOptionPane.showMessageDialog(this, "Insufficient Cash! Need: RM " + String.format("%.2f", selectedTotal));
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Cash Amount");
                    return;
                }
            }

            ExitSystem exitSystem = new ExitSystem();
            String method = methodDropdown.getSelectedItem().equals("Cash") ? "CASH" : "CARD";

            Receipt receipt = exitSystem.confirmExit(
                pendingExit.getLicensePlate(),
                pendingExit.getParkingFee(),
                fineAmountToPay,
                method
            );

            if (receipt != null) {
                new ReceiptPageUI(receipt).setVisible(true);
                dispose();
            } else {
                double newFee = pendingExit.getParkingFee();
                double newFines = pendingExit.getCurrentFines();

                if (Math.abs(newFee - oldFee) > 0.01 || newFines > oldFines) {
                    JOptionPane.showMessageDialog(this,
                        "Alert: Time threshold crossed! Fees have been updated.\nPlease review the new total and confirm payment again.",
                        "Fees Updated", JOptionPane.WARNING_MESSAGE);

                    this.parkingFee = newFee;
                    this.totalFines = newFines + pendingExit.getUnpaidFines();

                    feeOnlyAmount.setText("RM " + String.format("%.2f", parkingFee));
                    fullTotal.setText("Total: RM " + String.format("%.2f", parkingFee + totalFines));

                    full.setSelected(true);
                    selectedTotal = parkingFee + totalFines;
                    fineAmountToPay = totalFines;
                    partialInput.setEnabled(false);
                    partialInput.setText("");

                } else {
                    JOptionPane.showMessageDialog(this, "Payment Failed. Database Error.");
                }
            }
        });

        cancel.addActionListener(e -> {
            new MainMenuUI().setVisible(true);
            dispose();
        });

        btnRow.add(confirm);
        btnRow.add(cancel);
        mainCard.add(btnRow);
    }

    private RoundedPanel createBox(){
        RoundedPanel p = new RoundedPanel(20);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(255,255,255,120));
        p.setBorder(new EmptyBorder(15,20,15,20));
        return p;
    }

    private JLabel centerHeader(String text){
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Arial", Font.BOLD, 15));
        l.setForeground(new Color(2,52,63));
        return l;
    }

    private JLabel centerText(String text){
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(2,52,63));
        return l;
    }

    private JPanel centerRow(JComponent comp){
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.add(comp);
        return row;
    }

    private void styleRadio(JRadioButton r){
        r.setForeground(new Color(2,52,63));
        r.setOpaque(false);
        r.setFont(new Font("Arial", Font.BOLD, 13));
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new PaymentPageUI().setVisible(true));
    }
}
