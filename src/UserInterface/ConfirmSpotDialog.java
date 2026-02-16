package UserInterface;

import EntryModule.EntryController;
import EntryModule.Vehicle;
import java.awt.*;
import javax.swing.*;

public class ConfirmSpotDialog extends JDialog {

    public ConfirmSpotDialog(JFrame parent, String spotId) {
        super(parent, true);
        setSize(300, 360);
        setLocationRelativeTo(parent);
        setUndecorated(true);
        setBackground(new Color(0,0,0,0));

        // transparent wrapper to center the card
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        add(wrapper);

        // main dialog card
        RoundedPanel card = new RoundedPanel(25);
        card.setBackground(ThemeColors.SECONDARY);
        card.setPreferredSize(new Dimension(240,310));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15,25,15,25));

        JLabel t1 = createLabel("You");
        JLabel t2 = createLabel("are about to");
        JLabel t3 = createLabel("park at");

        // display selected spot id clearly
        JLabel spot = new JLabel(spotId);
        spot.setAlignmentX(Component.CENTER_ALIGNMENT);
        spot.setHorizontalAlignment(SwingConstants.CENTER);
        spot.setFont(new Font("Arial",Font.BOLD,16));
        spot.setForeground(ThemeColors.PRIMARY);
        spot.setMaximumSize(new Dimension(220,30));

        JButton cancel = new RoundedButton("Cancel", ThemeColors.PRIMARY);
        JButton confirm = new RoundedButton("Confirm", ThemeColors.PRIMARY);

        cancel.setForeground(ThemeColors.SECONDARY);
        confirm.setForeground(ThemeColors.SECONDARY);

        cancel.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirm.setAlignmentX(Component.CENTER_ALIGNMENT);

        cancel.setMaximumSize(new Dimension(140,38));
        confirm.setMaximumSize(new Dimension(140,38));

        // close dialog without doing anything
        cancel.addActionListener(e -> dispose());

        // confirm parking and send request to backend
        confirm.addActionListener(e -> {
            Vehicle vehicle = null;

            // get vehicle object from parent SpotSelectionUI
            if (parent instanceof SpotSelectionUI) {
                vehicle = ((SpotSelectionUI) parent).currentVehicle;
            }

            if (vehicle == null) {
                JOptionPane.showMessageDialog(this, "Error: Vehicle data missing.");
                return;
            }

            // convert UI spot format to backend format
            String backendSpotId = spotId.replace("Floor ", "")
                                         .replace(" Row ", "-")
                                         .replace(" Spot ", "-");

            EntryController controller = new EntryController();
            String result = controller.attemptPark(vehicle, backendSpotId);

            // if success, close both dialogs
            if (result.startsWith("SUCCESS")) {
                dispose(); 
                parent.dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, result, "Parking Failed", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        });

        card.add(Box.createVerticalStrut(30));
        card.add(t1);
        card.add(t2);
        card.add(t3);
        card.add(Box.createVerticalStrut(10));
        card.add(spot);
        card.add(Box.createVerticalStrut(30));
        card.add(cancel);
        card.add(Box.createVerticalStrut(12));
        card.add(confirm);

        wrapper.add(card);
    }

    // helper method to create centered labels with consistent styling
    private JLabel createLabel(String text){
        JLabel l=new JLabel(text);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setForeground(ThemeColors.PRIMARY);
        l.setMaximumSize(new Dimension(200,25));
        return l;
    }
}
