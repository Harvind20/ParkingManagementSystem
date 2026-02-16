package UserInterface;

import coreParkingSystem.AdminSettingsDAO;
import coreParkingSystem.DatabaseConnection;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class AnalyticsPanel extends JPanel {

    private JLabel totalSpotsLbl;
    private JLabel occupiedLbl;
    private JLabel availableLbl;
    private JLabel reservedLbl;
    private JLabel utilizationLbl;

    private JLabel parkingFeesLbl;
    private JLabel finesLbl;
    private JLabel totalRevenueLbl;
    
    private JLabel activeSchemeLbl; 

    public AnalyticsPanel() {

        setLayout(new BorderLayout());
        setBackground(ThemeColors.PRIMARY);

        // main container holding left and right analytics cards
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setBackground(ThemeColors.PRIMARY);
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        content.add(createLeftSection());
        content.add(createRightSection());

        add(content, BorderLayout.CENTER);

        // load data immediately and refresh every 10 seconds
        updateAnalytics();
        new javax.swing.Timer(10000, e -> updateAnalytics()).start();
    }

    private JPanel createLeftSection() {

        // card showing occupancy and revenue info
        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(ThemeColors.SECONDARY);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel left = new JPanel();
        left.setBackground(ThemeColors.SECONDARY);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Occupancy Status", SwingConstants.CENTER);
        title.setForeground(ThemeColors.PRIMARY);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(title);
        left.add(Box.createVerticalStrut(12));

        // labels that will be updated from DB
        totalSpotsLbl = createTextPrimary("Total Spots:");
        occupiedLbl = createTextPrimary("Occupied:");
        availableLbl = createTextPrimary("Available:");
        reservedLbl = createTextPrimary("Reserved:");

        left.add(totalSpotsLbl);
        left.add(occupiedLbl);
        left.add(availableLbl);
        left.add(reservedLbl);

        left.add(Box.createVerticalStrut(12));
        left.add(createDividerPrimary());
        left.add(Box.createVerticalStrut(12));

        utilizationLbl = createTextPrimary("Current Utilization:");
        left.add(utilizationLbl);
        left.add(Box.createVerticalStrut(30));

        JLabel revTitle = new JLabel("Revenue Summary", SwingConstants.CENTER);
        revTitle.setForeground(ThemeColors.PRIMARY);
        revTitle.setFont(new Font("Arial", Font.BOLD, 17));
        revTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(revTitle);

        // note to indicate revenue is filtered to today only
        JLabel todayNote = new JLabel("Showing today's data only", SwingConstants.CENTER);
        todayNote.setForeground(ThemeColors.PRIMARY);
        todayNote.setFont(new Font("Arial", Font.ITALIC, 12));
        todayNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        left.add(todayNote);

        left.add(Box.createVerticalStrut(12));

        parkingFeesLbl = createTextPrimary("Parking Fees:");
        finesLbl = createTextPrimary("Fines Collection:");
        left.add(parkingFeesLbl);
        left.add(finesLbl);

        left.add(Box.createVerticalStrut(12));
        left.add(createDividerPrimary());
        left.add(Box.createVerticalStrut(12));

        totalRevenueLbl = createTextPrimary("Total Revenue:");
        left.add(totalRevenueLbl);

        card.add(left, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRightSection() {

        // card for selecting fine calculation strategy
        RoundedPanel card = new RoundedPanel(30);
        card.setBackground(ThemeColors.SECONDARY);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel right = new JPanel();
        right.setBackground(ThemeColors.SECONDARY);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Fine Calculation Scheme", SwingConstants.CENTER);
        title.setForeground(ThemeColors.PRIMARY);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        right.add(title);

        // shows current scheme pulled from DB
        activeSchemeLbl = new JLabel("Active Scheme: Loading...", SwingConstants.CENTER);
        activeSchemeLbl.setForeground(ThemeColors.PRIMARY);
        activeSchemeLbl.setFont(new Font("Arial", Font.PLAIN, 14));
        activeSchemeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        right.add(activeSchemeLbl);
        right.add(Box.createVerticalStrut(6));
        right.add(createDividerPrimary());
        right.add(Box.createVerticalStrut(14));

        JRadioButton fixed = new JRadioButton("Fixed Fine Scheme");
        JRadioButton progressive = new JRadioButton("Progressive Fine Scheme");
        JRadioButton hourly = new JRadioButton("Hourly Fine Scheme");

        styleRadioPrimary(fixed);
        styleRadioPrimary(progressive);
        styleRadioPrimary(hourly);

        ButtonGroup group = new ButtonGroup();
        group.add(fixed);
        group.add(progressive);
        group.add(hourly);

        // set selected radio based on current value stored in DB
        String currentStrategy = new AdminSettingsDAO().getCurrentStrategy();
        if ("HOURLY".equalsIgnoreCase(currentStrategy)) {
            hourly.setSelected(true);
        }
        else if ("PROGRESSIVE".equalsIgnoreCase(currentStrategy)) {
            progressive.setSelected(true);
        }
        else {
            fixed.setSelected(true);
        }

        // open confirmation dialog when changing scheme
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
        right.add(createTextPrimary("Flat RM 50 fine for overstaying"));
        right.add(Box.createVerticalStrut(14));

        right.add(progressive);
        right.add(createTextPrimary("First 24 hours: RM 50"));
        right.add(createTextPrimary("24–48 hours: +RM 100"));
        right.add(createTextPrimary("48–72 hours: +RM 150"));
        right.add(createTextPrimary("Above 72 hours: +RM 200"));
        right.add(Box.createVerticalStrut(14));

        right.add(hourly);
        right.add(createTextPrimary("RM 20 per hour for each extra hour"));

        card.add(right, BorderLayout.CENTER);
        return card;
    }

    private JLabel createTextPrimary(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setForeground(ThemeColors.PRIMARY);
        l.setFont(new Font("Arial", Font.PLAIN, 15));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    // styling for radio buttons
    private void styleRadioPrimary(JRadioButton r) {
        r.setForeground(ThemeColors.PRIMARY);
        r.setOpaque(false);
        r.setFont(new Font("Arial", Font.BOLD, 17));
        r.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    // divider used to visually separate sections
    private JSeparator createDividerPrimary() {
        JSeparator sep = new JSeparator();
        sep.setForeground(ThemeColors.PRIMARY);
        sep.setMaximumSize(new Dimension(420, 1));
        return sep;
    }

    // pulls latest occupancy and revenue data from DB and updates labels
    private void updateAnalytics() {
    try {
        Connection conn = DatabaseConnection.connect();

        // total parking spots
        PreparedStatement pst1 =
            conn.prepareStatement("SELECT COUNT(*) FROM parking_spots");
        ResultSet rs1 = pst1.executeQuery();
        int totalSpots = rs1.getInt(1);

        // currently occupied spots
        PreparedStatement pst2 =
            conn.prepareStatement("SELECT COUNT(*) FROM parking_spots WHERE status='OCCUPIED'");
        ResultSet rs2 = pst2.executeQuery();
        int occupied = rs2.getInt(1);

        // reserved spots count
        PreparedStatement pst3 =
            conn.prepareStatement("SELECT COUNT(*) FROM parking_spots WHERE type='RESERVED'");
        ResultSet rs3 = pst3.executeQuery();
        int reserved = rs3.getInt(1);

        int available = totalSpots - occupied;
        double utilization = (totalSpots > 0) ? (occupied * 100.0) / totalSpots : 0;

        // revenue filtered to today only
        String dateFilter = "WHERE date(exit_time) = date('now')";

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

        // get active fine scheme name from DB
        String currentStrategyLabel = new AdminSettingsDAO().getCurrentStrategy();
        String displayStrategy = "Fixed Fine Scheme";
        if("HOURLY".equalsIgnoreCase(currentStrategyLabel)) displayStrategy = "Hourly Fine Scheme";
        if("PROGRESSIVE".equalsIgnoreCase(currentStrategyLabel)) displayStrategy = "Progressive Fine Scheme";

        // update UI labels
        totalSpotsLbl.setText("Total Spots: " + totalSpots);
        occupiedLbl.setText("Occupied: " + occupied);
        availableLbl.setText("Available: " + available);
        reservedLbl.setText("Reserved: " + reserved);
        utilizationLbl.setText("Current Utilization: " + String.format("%.1f%%", utilization));

        parkingFeesLbl.setText("Parking Fees: RM " + String.format("%.2f", parkingFees));
        finesLbl.setText("Fines Collection: RM " + String.format("%.2f", fines));
        totalRevenueLbl.setText("Total Revenue: RM " + String.format("%.2f", totalRevenue));
        
        activeSchemeLbl.setText("Active Scheme: " + displayStrategy);

        conn.close();

    } catch (Exception e) {
        e.printStackTrace();
        }
    }
}
