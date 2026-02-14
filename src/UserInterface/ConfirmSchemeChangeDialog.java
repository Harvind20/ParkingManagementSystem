package UserInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConfirmSchemeChangeDialog extends JDialog {

    public ConfirmSchemeChangeDialog(JFrame parent, String schemeName) {
        super(parent, "Confirm Scheme Change", true);

        setSize(650, 350);
        setLocationRelativeTo(parent);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(ThemeColors.PRIMARY);

        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(ThemeColors.SECONDARY);
        card.setPreferredSize(new Dimension(560, 250));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        add(card);

        // Title
        JLabel title = centerText("Confirm Fine Scheme Change", 16, true);
        card.add(title);
        card.add(Box.createVerticalStrut(12));

        // Description
        card.add(centerText("You are about to change the fine calculation policy.", 13, false));
        card.add(Box.createVerticalStrut(12));

        JLabel scheme = centerText("New Scheme: " + schemeName, 14, true);
        card.add(scheme);

        card.add(Box.createVerticalStrut(12));
        card.add(centerText("This will affect all future fine calculations.", 13, false));

        card.add(Box.createVerticalStrut(25));

        // Buttons row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        btnRow.setOpaque(false);

        RoundedButton confirm = new RoundedButton("Confirm", new Color(120,200,80));
        RoundedButton cancel = new RoundedButton("Cancel", new Color(255,60,60));

        confirm.setPreferredSize(new Dimension(140, 45));
        cancel.setPreferredSize(new Dimension(140, 45));

        confirm.addActionListener(e -> {
            // update DB here
            dispose();
        });

        cancel.addActionListener(e -> dispose());

        btnRow.add(confirm);
        btnRow.add(cancel);

        card.add(btnRow);
    }

    private JLabel centerText(String text, int size, boolean bold) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(ThemeColors.PRIMARY);
        return l;
    }
}
