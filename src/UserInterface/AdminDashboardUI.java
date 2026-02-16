package UserInterface;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUI extends JFrame {

    public AdminDashboardUI() {

        // Basic window setup
        setTitle("Admin Dashboard");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main container for the dashboard
        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(ThemeColors.PRIMARY);
        add(background);

        // Open dashboard in fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Tabs to separate different admin features
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeColors.SECONDARY);

        // Tabs for the panels
        tabs.addTab("Lot Status", new LotStatusPanel());
        tabs.addTab("Analytics", new AnalyticsPanel());
        tabs.addTab("Parked Vehicles", new ParkedVehiclesPanel());
        tabs.addTab("Outstanding Fines", new OutstandingFinesPanel());
        tabs.addTab("Report", new ReportPanel());

        background.add(tabs);
    }
}
