package UserInterface;

import EntryModule.*;
import coreParkingSystem.ParkingLot;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class EntryPageUI extends JFrame {

    public EntryPageUI() {
        setTitle("Parking Entry");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        Color DARK = new Color(0x02,0x34,0x3F);
        Color CREAM = new Color(0xF0,0xED,0xCC);

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(DARK);

        JButton returnBtn = new RoundedButton("Return", CREAM);
        returnBtn.setForeground(DARK);
        returnBtn.setPreferredSize(new Dimension(100, 35));

        returnBtn.addActionListener(e -> {
            new MainMenuUI().setVisible(true);
            dispose();
        });

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topBar.setBackground(DARK);
        topBar.add(returnBtn);

        background.add(topBar, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        RoundedPanel titleBox = new RoundedPanel(25);
        titleBox.setBackground(CREAM);
        titleBox.setPreferredSize(new Dimension(320, 70));
        titleBox.setLayout(new GridLayout(2, 1));

        JLabel title = new JLabel("Parking Entry", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(DARK);

        JLabel subtitle = new JLabel("Enter Vehicle Details", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setForeground(DARK);

        titleBox.add(title);
        titleBox.add(subtitle);

        gbc.gridy = 0;
        centerPanel.add(titleBox, gbc);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 15));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel l1 = new JLabel("License Plate:");
        l1.setForeground(CREAM);
        l1.setFont(new Font("Arial", Font.BOLD, 14));
        
        RoundedTextField plateField = new RoundedTextField(15);
        plateField.setText("PLT" + (int)(Math.random()*1000));

        JLabel l2 = new JLabel("Vehicle Type:");
        l2.setForeground(CREAM);
        l2.setFont(new Font("Arial", Font.BOLD, 14));
        
        String[] types = {"Car", "SUV", "Motorcycle", "HandicappedVehicle"};
        JComboBox<String> typeBox = new JComboBox<>(types);

        JCheckBox vipCheck = new JCheckBox("VIP Member");
        vipCheck.setOpaque(false);
        vipCheck.setForeground(CREAM);
        
        JCheckBox handicapCheck = new JCheckBox("Handicapped Driver");
        handicapCheck.setOpaque(false);
        handicapCheck.setForeground(CREAM);

        formPanel.add(l1); formPanel.add(plateField);
        formPanel.add(l2); formPanel.add(typeBox);
        formPanel.add(vipCheck); formPanel.add(handicapCheck);

        gbc.gridy = 1;
        centerPanel.add(formPanel, gbc);

        RoundedButton nextBtn = new RoundedButton("Park", CREAM); 
        nextBtn.setForeground(DARK);
        nextBtn.setPreferredSize(new Dimension(120, 40));

        nextBtn.addActionListener(e -> {
            String plate = plateField.getText().trim();
            String vType = (String) typeBox.getSelectedItem();
            boolean isVip = vipCheck.isSelected();
            boolean isHandicap = handicapCheck.isSelected();
            int specialCharacterCount = 0;
            int numOfDigits = 0;

            plate = plate.toUpperCase();
            if(plate.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please enter a License Plate.");
                return;
            }
            
            for (int i = 0; i < plate.length(); i++){
                char c = plate.charAt(i);
                if (!Character.isLetterOrDigit(c) && !Character.isSpaceChar(c)){
                    specialCharacterCount += 1;
                }
                else if((Character.isDigit(c))){
                    numOfDigits += 1;
                }
            }

            if (specialCharacterCount > 0){
                JOptionPane.showMessageDialog(this, "No special characters allowed in license plate (Spaces are allowed).");
                return;
            }
            else if (numOfDigits <= 0){
                JOptionPane.showMessageDialog(this, "License plate requires at least one number.");
                return;
            }

            // Check for duplicate ACTIVE tickets
            if (ParkingLot.getInstance().getTicketByPlate(plate) != null) {
                JOptionPane.showMessageDialog(this, "Error: This vehicle already has an ACTIVE ticket.");
                return;
            }

            Vehicle vehicle;
            switch (vType) {
                case "SUV": vehicle = new SUV(plate); break;
                case "Motorcycle": vehicle = new Motorcycle(plate); break;
                case "HandicappedVehicle": vehicle = new HandicappedVehicle(plate); break;
                default: vehicle = new Car(plate);
            }

            vehicle.setVip(isVip);
            SpotSelectionUI spotUI = new SpotSelectionUI(vehicle);
            spotUI.vipEnabled = isVip;
            spotUI.handicapEnabled = isHandicap || (vehicle instanceof HandicappedVehicle);
            
            spotUI.setVisible(true);
            dispose(); 
        });

        gbc.gridy = 2;
        centerPanel.add(nextBtn, gbc);

        background.add(centerPanel, BorderLayout.CENTER);
        add(background);
    }

    class RoundedTextField extends JTextField {
        private int radius;
        RoundedTextField(int radius) {
            this.radius = radius;
            setOpaque(false);
            setBorder(new EmptyBorder(5, 10, 5, 10));
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0xF0,0xED,0xCC));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}