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

        Color DARK = new Color(0x02,0x34,0x3F);
        Color CREAM = new Color(0xF0,0xED,0xCC);

        JPanel background = new JPanel();
        background.setBackground(DARK);
        background.setLayout(new GridBagLayout());

        RoundedPanel box = new RoundedPanel(30);
        box.setPreferredSize(new Dimension(240, 340));
        box.setBackground(CREAM);
        box.setLayout(new GridLayout(3, 1, 15, 25));
        box.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        JButton entryBtn = new RoundedButton("Entry", DARK);
        JButton exitBtn = new RoundedButton("Exit", DARK);
        JButton adminBtn = new RoundedButton("Admin", DARK);

        entryBtn.setForeground(CREAM);
        exitBtn.setForeground(CREAM);
        adminBtn.setForeground(CREAM);

        box.add(entryBtn);
        box.add(exitBtn);
        box.add(adminBtn);

        background.add(box);
        add(background);

        entryBtn.addActionListener(e -> {
            new EntryPageUI().setVisible(true);
            dispose();
        });

        exitBtn.addActionListener(e -> {
             new ExitPageUI().setVisible(true); 
             dispose();
        });

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