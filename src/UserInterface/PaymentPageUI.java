package UserInterface;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PaymentPageUI extends JFrame {

    public PaymentPageUI() {

        setTitle("Payment");
        setSize(900,650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel background = new JPanel(new GridBagLayout());
        background.setBackground(new Color(2,52,63)); // #02343F
        add(background);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(new Color(240,237,204)); // #F0EDCC
        card.setPreferredSize(new Dimension(760,600));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25,40,25,40));

        background.add(card);

        // Payment Method
        card.add(centerText("Select Payment Method:"));
        card.add(Box.createVerticalStrut(10));

        JRadioButton cash = new JRadioButton("Cash");
        JRadioButton cardPay = new JRadioButton("Card");

        styleRadio(cash);
        styleRadio(cardPay);

        ButtonGroup methodGroup = new ButtonGroup();
        methodGroup.add(cash);
        methodGroup.add(cardPay);

        card.add(centerRow(cash));
        card.add(centerRow(cardPay));

        card.add(Box.createVerticalStrut(25));

        // Payment Amount
        card.add(centerText("Select Payment Amount:"));
        card.add(Box.createVerticalStrut(10));

        // Option 1
        JRadioButton feeOnly = new JRadioButton("Pay Ticket Fee Only");
        styleRadio(feeOnly);
        JLabel feeOnlyAmount = centerText("RM ");

        // Option 2
        JRadioButton partial = new JRadioButton("Ticket Fee + Partial Fine Payment");
        styleRadio(partial);

        JPanel partialRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        partialRow.setOpaque(false);
        JLabel partialLabel = new JLabel("Amount to pay towards fines: RM ");
        partialLabel.setForeground(new Color(2,52,63));
        JTextField partialInput = new JTextField(8);
        partialRow.add(partialLabel);
        partialRow.add(partialInput);

        JLabel partialTotal = centerText("Total: RM ");

        // Option 3
        JRadioButton full = new JRadioButton("Ticket Fee + Pay All Outstanding Fines");
        styleRadio(full);
        JLabel fullTotal = centerText("Total: RM ");

        ButtonGroup amountGroup = new ButtonGroup();
        amountGroup.add(feeOnly);
        amountGroup.add(partial);
        amountGroup.add(full);

        card.add(centerRow(feeOnly));
        card.add(feeOnlyAmount);
        card.add(Box.createVerticalStrut(15));

        card.add(centerRow(partial));
        card.add(partialRow);
        card.add(partialTotal);
        card.add(Box.createVerticalStrut(15));

        card.add(centerRow(full));
        card.add(fullTotal);

        card.add(Box.createVerticalStrut(25));

        // Input cash
        JPanel cashPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        cashPanel.setOpaque(false);

        JLabel cashLabel = new JLabel("Enter Cash Amount: RM ");
        cashLabel.setForeground(new Color(2,52,63));

        JTextField cashInput = new JTextField(8);

        cashPanel.add(cashLabel);
        cashPanel.add(cashInput);

        cashPanel.setVisible(false);
        card.add(cashPanel);

        cash.addActionListener(e -> cashPanel.setVisible(true));
        cardPay.addActionListener(e -> cashPanel.setVisible(false));

        card.add(Box.createVerticalStrut(25));

        // Buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER,25,0));
        btnRow.setOpaque(false);

        RoundedButton confirm = new RoundedButton("Confirm", new Color(120,200,80));
        RoundedButton cancel = new RoundedButton("Cancel", new Color(255,60,60));

        confirm.setPreferredSize(new Dimension(140,45));
        cancel.setPreferredSize(new Dimension(140,45));

        // Redirect to ReceiptPageUI
        confirm.addActionListener(e -> {
            new ReceiptPageUI().setVisible(true);
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        btnRow.add(confirm);
        btnRow.add(cancel);

        card.add(btnRow);
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
