package UserInterface;

import coreParkingSystem.ParkingLot;
import java.awt.*;
import javax.swing.*;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {
        setTitle("Parking System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // open main menu in fullscreen
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Background panel 
        JPanel background = new JPanel();
        background.setBackground(ThemeColors.PRIMARY);
        background.setLayout(new GridBagLayout());

        RoundedPanel box = new RoundedPanel(30);
        box.setPreferredSize(new Dimension(240, 340));
        box.setBackground(ThemeColors.SECONDARY);
        box.setLayout(new GridLayout(3, 1, 15, 25));
        box.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        // Navigation buttons
        JButton entryBtn = new RoundedButton("Entry", ThemeColors.PRIMARY);
        JButton exitBtn = new RoundedButton("Exit", ThemeColors.PRIMARY);
        JButton adminBtn = new RoundedButton("Admin", ThemeColors.PRIMARY);

        entryBtn.setForeground(ThemeColors.SECONDARY);
        exitBtn.setForeground(ThemeColors.SECONDARY);
        adminBtn.setForeground(ThemeColors.SECONDARY);

        box.add(entryBtn);
        box.add(exitBtn);
        box.add(adminBtn);

        background.add(box);
        add(background);

        // opens vehicle entry screen
        entryBtn.addActionListener(e -> {
            new EntryPageUI().setVisible(true);
            dispose();
        });

        // opens vehicle exit screen
        exitBtn.addActionListener(e -> {
             new ExitPageUI().setVisible(true); 
             dispose();
        });

        // opens admin dashboard
        adminBtn.addActionListener(e -> {
            new AdminDashboardUI().setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        ParkingLot.getInstance();

        SwingUtilities.invokeLater(() -> {
            new MainMenuUI().setVisible(true);
        });
    }
}
