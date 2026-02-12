import javax.swing.*;
import java.awt.*;

public class MainMenuUI extends JFrame {

    public MainMenuUI() {
        setTitle("Parking System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background panel
        JPanel background = new JPanel();
        background.setBackground(new Color(10, 70, 100));
        background.setLayout(new GridBagLayout());

        // Rounded box panel
        RoundedPanel box = new RoundedPanel(30);
        box.setPreferredSize(new Dimension(240, 340));
        box.setBackground(Color.LIGHT_GRAY);
        box.setLayout(new GridLayout(3, 1, 15, 25));
        box.setBorder(BorderFactory.createEmptyBorder(40, 30, 40, 30));

        // Buttons
        JButton entryBtn = createRoundedButton("Entry");
        JButton exitBtn = createRoundedButton("Exit");
        JButton adminBtn = createRoundedButton("Admin");

        box.add(entryBtn);
        box.add(exitBtn);
        box.add(adminBtn);

        background.add(box);
        add(background);

        // Temporary actions
        entryBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Entry pressed")
        );

        exitBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Exit pressed")
        );

        adminBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Admin pressed")
        );
    }

    // Rounded button creator
    private JButton createRoundedButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(120, 220, 90));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton b = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(b.getBackground());
                g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), 30, 30);

                super.paint(g2, c);
                g2.dispose();
            }
        });

        return button;
    }

    // Rounded panel class
    class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int radius) {
            super();
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                    cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainMenuUI().setVisible(true);
        });
    }
}
