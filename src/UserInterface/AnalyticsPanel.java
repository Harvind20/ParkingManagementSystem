package UserInterface;

import javax.swing.*;
import java.awt.*;

public class AnalyticsPanel extends JPanel {

    public AnalyticsPanel() {

        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        JPanel content = new JPanel(new GridLayout(1, 2));
        content.setBackground(ThemeColors.PRIMARY);

        content.add(createLeftSection());
        content.add(createRightSection());

        add(content, BorderLayout.CENTER);
    }

    private JPanel createLeftSection() {

        JPanel left = new JPanel();
        left.setBackground(ThemeColors.PRIMARY);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 30));

        JLabel title = new JLabel("Occupancy Status");
        title.setForeground(ThemeColors.SECONDARY);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(title);
        left.add(Box.createVerticalStrut(12));

        left.add(createText("Total Spots:"));
        left.add(createText("Occupied:"));
        left.add(createText("Available:"));
        left.add(createText("Reserved:"));

        left.add(Box.createVerticalStrut(12));
        left.add(createDivider());
        left.add(Box.createVerticalStrut(12));

        left.add(createText("Current Utilization:"));
        left.add(Box.createVerticalStrut(30));

        JLabel revTitle = new JLabel("Revenue Summary");
        revTitle.setForeground(ThemeColors.SECONDARY);
        revTitle.setFont(new Font("Arial", Font.BOLD, 17));
        revTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(revTitle);

        left.add(Box.createVerticalStrut(12));

        JComboBox<String> periodDropdown = new JComboBox<>(new String[]{
                "Today",
                "Last Week",
                "Last Month"
        });
        periodDropdown.setMaximumSize(new Dimension(180, 32));
        periodDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        periodDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(periodDropdown);
        left.add(Box.createVerticalStrut(18));

        left.add(createText("Parking Fees:"));
        left.add(createText("Fines Collection:"));

        left.add(Box.createVerticalStrut(12));
        left.add(createDivider());
        left.add(Box.createVerticalStrut(12));

        left.add(createText("Total Revenue:"));

        return left;
    }

    private JPanel createRightSection() {

        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(ThemeColors.PRIMARY);

        JPanel divider = new JPanel();
        divider.setBackground(ThemeColors.SECONDARY);
        divider.setPreferredSize(new Dimension(2, 0));
        rightWrapper.add(divider, BorderLayout.WEST);

        JPanel right = new JPanel();
        right.setBackground(ThemeColors.PRIMARY);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 60));

        JLabel title = new JLabel("Fine Calculation Scheme");
        title.setForeground(ThemeColors.SECONDARY);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        right.add(title);

        JLabel active = new JLabel("Active Scheme: Fixed Fine Scheme");
        active.setForeground(ThemeColors.SECONDARY);
        active.setFont(new Font("Arial", Font.PLAIN, 14));
        active.setAlignmentX(Component.LEFT_ALIGNMENT);

        right.add(active);
        right.add(Box.createVerticalStrut(6));
        right.add(createDivider());
        right.add(Box.createVerticalStrut(14));

        JRadioButton fixed = new JRadioButton("Fixed Fine Scheme");
        JRadioButton progressive = new JRadioButton("Progressive Fine Scheme");
        JRadioButton hourly = new JRadioButton("Hourly Fine Scheme");

        styleRadio(fixed);
        styleRadio(progressive);
        styleRadio(hourly);

        ButtonGroup group = new ButtonGroup();
        group.add(fixed);
        group.add(progressive);
        group.add(hourly);

        fixed.setSelected(true);

        // Opens confirmation dialog when clicked
        fixed.addActionListener(e ->
            new ConfirmSchemeChangeDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Fixed Fine Scheme"
            ).setVisible(true)
        );

        progressive.addActionListener(e ->
            new ConfirmSchemeChangeDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Progressive Fine Scheme"
            ).setVisible(true)
        );

        hourly.addActionListener(e ->
            new ConfirmSchemeChangeDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "Hourly Fine Scheme"
            ).setVisible(true)
        );

        right.add(fixed);
        right.add(createText("Flat RM 50 fine for overstaying"));
        right.add(Box.createVerticalStrut(14));

        right.add(progressive);
        right.add(createText("First 24 hours: RM 50"));
        right.add(createText("24–48 hours: +RM 100"));
        right.add(createText("48–72 hours: +RM 150"));
        right.add(createText("Above 72 hours: +RM 200"));
        right.add(Box.createVerticalStrut(14));

        right.add(hourly);
        right.add(createText("RM 20 per hour for each extra hour"));

        rightWrapper.add(right, BorderLayout.CENTER);

        return rightWrapper;
    }

    private JLabel createText(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(ThemeColors.SECONDARY);
        l.setFont(new Font("Arial", Font.PLAIN, 15));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleRadio(JRadioButton r) {
        r.setForeground(ThemeColors.SECONDARY);
        r.setOpaque(false);
        r.setFont(new Font("Arial", Font.BOLD, 15));
        r.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JSeparator createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.SECONDARY);
        sep.setMaximumSize(new Dimension(420, 1));
        return sep;
    }
}
