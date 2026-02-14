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

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(ThemeColors.SECONDARY);

        tabs.addTab("Lot Status", new LotStatusPanel());
        tabs.addTab("Analytics", new AnalyticsPanel());
        tabs.addTab("Analytics", new ParkedVehiclesPanel());
        tabs.addTab("Outstanding Fines", new OutstandingFinesPanel());

        background.add(tabs);
    }

    private JPanel createPlaceholder(String text){
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(ThemeColors.PRIMARY);

        JLabel label = new JLabel(text);
        label.setForeground(ThemeColors.SECONDARY);
        label.setFont(new Font("Arial", Font.BOLD, 18));

        p.add(label);
        return p;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new AdminDashboardUI().setVisible(true));
    }
}
