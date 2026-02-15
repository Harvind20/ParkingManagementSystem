package UserInterface;

import coreParkingSystem.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class AnalyticsPanel extends JPanel {

    private JComboBox<String> periodDropdown;
    private JLabel totalSpotsLbl;
    private JLabel occupiedLbl;
    private JLabel availableLbl;
    private JLabel reservedLbl;
    private JLabel utilizationLbl;

    private JLabel parkingFeesLbl;
    private JLabel finesLbl;
    private JLabel totalRevenueLbl;


    public AnalyticsPanel() {

        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        JPanel content = new JPanel(new GridLayout(1, 2));
        content.setBackground(ThemeColors.PRIMARY);

        content.add(createLeftSection());
        content.add(createRightSection());

        add(content, BorderLayout.CENTER);
        updateAnalytics();
        new javax.swing.Timer(10000, e -> updateAnalytics()).start();

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

        totalSpotsLbl = createText("Total Spots:");
        occupiedLbl = createText("Occupied:");
        availableLbl = createText("Available:");
        reservedLbl = createText("Reserved:");

        left.add(totalSpotsLbl);
        left.add(occupiedLbl);
        left.add(availableLbl);
        left.add(reservedLbl);


        left.add(Box.createVerticalStrut(12));
        left.add(createDivider());
        left.add(Box.createVerticalStrut(12));

        utilizationLbl = createText("Current Utilization:");
        left.add(utilizationLbl);
        left.add(Box.createVerticalStrut(30));

        JLabel revTitle = new JLabel("Revenue Summary");
        revTitle.setForeground(ThemeColors.SECONDARY);
        revTitle.setFont(new Font("Arial", Font.BOLD, 17));
        revTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(revTitle);

        left.add(Box.createVerticalStrut(12));

        periodDropdown = new JComboBox<>(new String[]{
                "Today",
                "Last Week",
                "Last Month"
        });
        periodDropdown.addActionListener(e -> updateAnalytics());
        periodDropdown.setMaximumSize(new Dimension(180, 32));
        periodDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        periodDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(periodDropdown);
        left.add(Box.createVerticalStrut(18));

        parkingFeesLbl = createText("Parking Fees:");
        finesLbl = createText("Fines Collection:");
        left.add(parkingFeesLbl);
        left.add(finesLbl);
        left.add(Box.createVerticalStrut(12));
        left.add(createDivider());
        left.add(Box.createVerticalStrut(12));

        totalRevenueLbl = createText("Total Revenue:");
        left.add(totalRevenueLbl);

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

    private void updateAnalytics() {
    try {
        Connection conn = DatabaseConnection.connect();

        // ===== OCCUPANCY SECTION =====

        // Total spots
        PreparedStatement pst1 =
            conn.prepareStatement("SELECT COUNT(*) FROM parking_spots");
        ResultSet rs1 = pst1.executeQuery();
        int totalSpots = rs1.getInt(1);

        // Occupied spots
        PreparedStatement pst2 =
            conn.prepareStatement("SELECT COUNT(*) FROM parking_spots WHERE status='OCCUPIED'");
        ResultSet rs2 = pst2.executeQuery();
        int occupied = rs2.getInt(1);

        // Reserved spots
        PreparedStatement pst3 =
            conn.prepareStatement("SELECT COUNT(*) FROM parking_spots WHERE type='RESERVED'");
        ResultSet rs3 = pst3.executeQuery();
        int reserved = rs3.getInt(1);

        int available = totalSpots - occupied;
        double utilization = (occupied * 100.0) / totalSpots;

        // ===== REVENUE FILTER =====
        String period = (String) periodDropdown.getSelectedItem();

        String dateFilter = "";
        if(period.equals("Today")) {
            dateFilter = "WHERE date(exit_time) = date('now')";
        }
        else if(period.equals("Last Week")) {
            dateFilter = "WHERE exit_time >= date('now','-7 days')";
        }
        else if(period.equals("Last Month")) {
            dateFilter = "WHERE exit_time >= date('now','-30 days')";
        }

        // ===== REVENUE QUERY =====
        PreparedStatement pst4 =
            conn.prepareStatement(
                "SELECT " +
                "IFNULL(SUM(parking_fee),0), " +
                "IFNULL(SUM(fine_amount),0), " +
                "IFNULL(SUM(total_paid),0) " +
                "FROM receipts " + dateFilter
            );

        ResultSet rs4 = pst4.executeQuery();

        double parkingFees = rs4.getDouble(1);
        double fines = rs4.getDouble(2);
        double totalRevenue = rs4.getDouble(3);

        // ===== UPDATE LABELS =====
        totalSpotsLbl.setText("Total Spots: " + totalSpots);
        occupiedLbl.setText("Occupied: " + occupied);
        availableLbl.setText("Available: " + available);
        reservedLbl.setText("Reserved: " + reserved);
        utilizationLbl.setText("Current Utilization: " + String.format("%.1f%%", utilization));

        parkingFeesLbl.setText("Parking Fees: RM " + String.format("%.2f", parkingFees));
        finesLbl.setText("Fines Collection: RM " + String.format("%.2f", fines));
        totalRevenueLbl.setText("Total Revenue: RM " + String.format("%.2f", totalRevenue));

        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
        }
    }



}
