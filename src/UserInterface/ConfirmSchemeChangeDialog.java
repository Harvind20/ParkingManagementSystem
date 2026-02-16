package UserInterface;

import FineModule.*;
import coreParkingSystem.AdminSettingsDAO;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ConfirmSchemeChangeDialog extends JDialog {

    public ConfirmSchemeChangeDialog(JFrame parent, String schemeName) {
        super(parent, "Confirm Scheme Change", true);

        setSize(650, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(ThemeColors.PRIMARY);

        // main card container for dialog content
        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(ThemeColors.SECONDARY);
        card.setPreferredSize(new Dimension(560, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        add(card);

        JLabel title = centerText("Confirm Fine Scheme Change", 16, true);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        card.add(centerText("You are about to change the fine calculation policy.", 13, false));
        card.add(Box.createVerticalStrut(12));

        // shows the selected scheme name
        JLabel scheme = centerText("New Scheme: " + schemeName, 14, true);
        card.add(scheme);

        card.add(Box.createVerticalStrut(12));
        card.add(centerText("This will affect all future fine calculations.", 13, false));

        card.add(Box.createVerticalStrut(25));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        btnRow.setOpaque(false);

        RoundedButton confirm = new RoundedButton("Confirm", new Color(120,200,80));
        RoundedButton cancel = new RoundedButton("Cancel", new Color(255,60,60));

        confirm.setPreferredSize(new Dimension(140, 45));
        cancel.setPreferredSize(new Dimension(140, 45));

        // determine which scheme was chosen and update DB 
        confirm.addActionListener(e -> {
            String dbValue = "FIXED";
            FineScheme strategyObj = new FixedFine();

            if (schemeName.contains("Hourly")) {
                dbValue = "HOURLY";
                strategyObj = new HourlyFine();
            } else if (schemeName.contains("Progressive")) {
                dbValue = "PROGRESSIVE";
                strategyObj = new ProgressiveFine();
            }

            // save selected scheme into database
            new AdminSettingsDAO().setStrategy(dbValue);

            // update the active fine calculation strategy in runtime
            FineManager.setFineScheme(strategyObj);

            dispose();
            JOptionPane.showMessageDialog(parent, "Fine Strategy Updated Successfully!");
        });

        // close dialog without making changes
        cancel.addActionListener(e -> dispose());

        btnRow.add(confirm);
        btnRow.add(cancel);

        card.add(btnRow);
    }

    // helper method to create centered labels with consistent styling
    private JLabel centerText(String text, int size, boolean bold) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(ThemeColors.PRIMARY);
        return l;
    }
}
