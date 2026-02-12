import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ExitPageUI extends JFrame {

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

        JTextField plateField = new JTextField("ABC1234  (Placeholder Text)");
        plateField.setBorder(new EmptyBorder(10,15,10,15));
        plateField.setOpaque(false);
        plateField.setForeground(new Color(2,52,63));
        plateFieldWrapper.add(plateField);

        RoundedButton findBtn = new RoundedButton("FIND", new Color(240,237,204));
        findBtn.setPreferredSize(new Dimension(90,50));

        findBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(this, "Search triggered (DB not connected yet)")
        );

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

        summaryCard.add(createText("Entry Time:"));
        summaryCard.add(createText("Exit Time:"));
        summaryCard.add(createText("Hours Parked:"));
        summaryCard.add(createText("Spot Type:"));
        summaryCard.add(createText("Vehicle Type:"));

        summaryCard.add(Box.createVerticalStrut(15));

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(2,52,63));
        sep1.setBackground(new Color(2,52,63));
        summaryCard.add(sep1);

        summaryCard.add(Box.createVerticalStrut(10));
        summaryCard.add(createText("Parking Fee:"));
        summaryCard.add(createText("Current Fine:"));
        summaryCard.add(createText("Unpaid Fines:"));

        summaryCard.add(Box.createVerticalStrut(10));

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(2,52,63));
        sep2.setBackground(new Color(2,52,63));
        summaryCard.add(sep2);

        summaryCard.add(Box.createVerticalStrut(10));
        summaryCard.add(createText("Total Due:"));

        gbc.gridy = 3;
        background.add(summaryCard, gbc);

        RoundedButton payBtn = new RoundedButton("Pay Now", new Color(240,237,204));
        payBtn.setPreferredSize(new Dimension(150,50));

        // Redirect to PaymentPageUI
        payBtn.addActionListener(e -> {
            new PaymentPageUI().setVisible(true);
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

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new ExitPageUI().setVisible(true));
    }
}
