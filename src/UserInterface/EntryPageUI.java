package UserInterface;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EntryPageUI extends JFrame {

    public EntryPageUI() {
        setTitle("Parking Entry");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Theme Colors
        Color DARK = new Color(0x02,0x34,0x3F);
        Color CREAM = new Color(0xF0,0xED,0xCC);

        JPanel background = new JPanel();
        background.setBackground(DARK);
        background.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title Box 
        RoundedPanel titleBox = new RoundedPanel(25);
        titleBox.setBackground(CREAM);
        titleBox.setPreferredSize(new Dimension(320, 70));
        titleBox.setLayout(new GridLayout(2, 1));

        JLabel title = new JLabel("Parking Entry", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel("Enter Vehicle Details", SwingConstants.CENTER);
        subtitle.setForeground(DARK);

        titleBox.add(title);
        titleBox.add(subtitle);

        gbc.gridy = 0;
        background.add(titleBox, gbc);

        // Number Plate Label
        JLabel plateLabel = new JLabel("Number Plate");
        plateLabel.setForeground(CREAM);
        gbc.gridy = 1;
        background.add(plateLabel, gbc);

        RoundedTextField plateField = new RoundedTextField(20);
        plateField.setPreferredSize(new Dimension(320, 40));
        plateField.setBackground(CREAM);
        plateField.setForeground(DARK);
        plateField.setText("ABC1234");
        gbc.gridy = 2;
        background.add(plateField, gbc);

        // Vehicle Type Label
        JLabel vehicleLabel = new JLabel("Vehicle Type");
        vehicleLabel.setForeground(CREAM);
        gbc.gridy = 3;
        background.add(vehicleLabel, gbc);

        // Dropdown
        String[] vehicles = {"Car", "SUV/Truck", "Motorcycle"};
        JComboBox<String> vehicleDropdown = new JComboBox<>(vehicles);
        vehicleDropdown.setPreferredSize(new Dimension(320, 40));
        vehicleDropdown.setBackground(CREAM);
        vehicleDropdown.setForeground(DARK);
        vehicleDropdown.setBorder(new RoundedBorder(20));
        gbc.gridy = 4;
        background.add(vehicleDropdown, gbc);

        // Checkboxes
        JCheckBox vipBox = new JCheckBox("VIP Customer");
        vipBox.setForeground(CREAM);
        vipBox.setBackground(DARK);

        JCheckBox handicapBox = new JCheckBox("Handicapped Card Holder");
        handicapBox.setForeground(CREAM);
        handicapBox.setBackground(DARK);

        gbc.gridy = 5;
        background.add(vipBox, gbc);

        gbc.gridy = 6;
        background.add(handicapBox, gbc);

        // Park Button 
        JButton parkBtn = new RoundedButton("Park →", CREAM);
        parkBtn.setForeground(DARK);
        parkBtn.setPreferredSize(new Dimension(160, 45));

        gbc.gridy = 7;
        gbc.insets = new Insets(30, 0, 0, 0);
        background.add(parkBtn, gbc);

        parkBtn.addActionListener(e -> {

            String plate = plateField.getText();
            String vehicle = (String) vehicleDropdown.getSelectedItem();
            boolean vip = vipBox.isSelected();
            boolean handicap = handicapBox.isSelected();

            // Open Spot Selection screen
            SpotSelectionUI spotUI = new SpotSelectionUI();

            // Pass VIP / Handicap settings to SpotSelection
            spotUI.vipEnabled = vip;
            spotUI.handicapEnabled = handicap;

            spotUI.setVisible(true);

            dispose();
        });

        add(background);
    }

    class RoundedTextField extends JTextField {
        private int radius;

        RoundedTextField(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0xF0,0xED,0xCC)); // cream
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(0x02,0x34,0x3F));
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EntryPageUI().setVisible(true);
        });
    }
}
