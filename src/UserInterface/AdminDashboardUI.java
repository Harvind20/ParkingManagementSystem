package UserInterface;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardUI extends JFrame {

    public AdminDashboardUI() {

        setTitle("Admin Dashboard");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(ThemeColors.PRIMARY);
        add(background);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeColors.SECONDARY);

        tabs.addTab("Lot Status", new LotStatusPanel());
        tabs.addTab("Analytics", new AnalyticsPanel());
        tabs.addTab("Parked Vehicles", new ParkedVehiclesPanel());
        tabs.addTab("Outstanding Fines", new OutstandingFinesPanel());
        tabs.addTab("Report", new ReportPanel());

        background.add(tabs);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new AdminDashboardUI().setVisible(true));
    }
}
