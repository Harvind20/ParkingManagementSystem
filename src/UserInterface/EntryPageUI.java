import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EntryPageUI extends JFrame {

    public EntryPageUI() {
        setTitle("Parking Entry");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = new JPanel();
        background.setBackground(new Color(10, 70, 100));
        background.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Title Box 
        RoundedPanel titleBox = new RoundedPanel(25);
        titleBox.setBackground(Color.GRAY);
        titleBox.setPreferredSize(new Dimension(320, 70));
        titleBox.setLayout(new GridLayout(2, 1));

        JLabel title = new JLabel("Parking Entry", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Enter Vehicle Details", SwingConstants.CENTER);
        subtitle.setForeground(Color.WHITE);

        titleBox.add(title);
        titleBox.add(subtitle);

        gbc.gridy = 0;
        background.add(titleBox, gbc);

        // Number Plate Label
        JLabel plateLabel = new JLabel("Number Plate");
        plateLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        background.add(plateLabel, gbc);

        // Rounded Text Field
        RoundedTextField plateField = new RoundedTextField(20);
        plateField.setPreferredSize(new Dimension(320, 40));
        plateField.setText("ABC1234");
        gbc.gridy = 2;
        background.add(plateField, gbc);

        // Vehicle Type Label
        JLabel vehicleLabel = new JLabel("Vehicle Type");
        vehicleLabel.setForeground(Color.WHITE);
        gbc.gridy = 3;
        background.add(vehicleLabel, gbc);

        // Rounded Dropdown
        String[] vehicles = {"Motorcycle", "Car", "SUV/Truck"};
        JComboBox<String> vehicleDropdown = new JComboBox<>(vehicles);
        vehicleDropdown.setPreferredSize(new Dimension(320, 40));
        vehicleDropdown.setBackground(Color.WHITE);
        vehicleDropdown.setBorder(new RoundedBorder(20));
        gbc.gridy = 4;
        background.add(vehicleDropdown, gbc);

        // Checkboxes 
        JCheckBox vipBox = new JCheckBox("VIP Customer");
        vipBox.setForeground(Color.WHITE);
        vipBox.setBackground(new Color(10, 70, 100));

        JCheckBox handicapBox = new JCheckBox("Handicapped Card Holder");
        handicapBox.setForeground(Color.WHITE);
        handicapBox.setBackground(new Color(10, 70, 100));

        gbc.gridy = 5;
        background.add(vipBox, gbc);

        gbc.gridy = 6;
        background.add(handicapBox, gbc);

        // Rounded Park Button 
        JButton parkBtn = new JButton("Park →");
        parkBtn.setPreferredSize(new Dimension(160, 45));
        parkBtn.setBackground(new Color(0, 180, 90));
        parkBtn.setFocusPainted(false);
        parkBtn.setContentAreaFilled(false);
        parkBtn.setOpaque(false);
        parkBtn.setBorder(new EmptyBorder(10, 20, 10, 20));
        parkBtn.setFont(new Font("Arial", Font.BOLD, 14));

        parkBtn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 180, 90));
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 30, 30);

                super.paint(g2, c);
                g2.dispose();
            }
        });

        gbc.gridy = 7;
        gbc.insets = new Insets(30, 0, 0, 0);
        background.add(parkBtn, gbc);

        // Test action
        parkBtn.addActionListener(e -> {
            String plate = plateField.getText();
            String vehicle = (String) vehicleDropdown.getSelectedItem();
            boolean vip = vipBox.isSelected();
            boolean handicap = handicapBox.isSelected();

            JOptionPane.showMessageDialog(this,
                    "Plate: " + plate +
                    "\nVehicle: " + vehicle +
                    "\nVIP: " + vip +
                    "\nHandicap: " + handicap);
        });

        add(background);
    }

    // Rounded Panel 
    class RoundedPanel extends JPanel {
        private int radius;

        RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g);
        }
    }

    // Rounded TextField
    class RoundedTextField extends JTextField {
        private int radius;

        RoundedTextField(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // Rounded Border for Dropdown 
    class RoundedBorder extends javax.swing.border.AbstractBorder {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // Test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EntryPageUI().setVisible(true);
        });
    }
}
