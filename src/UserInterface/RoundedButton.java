package UserInterface;
import java.awt.*;
import javax.swing.*;

public class RoundedButton extends JButton {

    Color color; // background color used to paint the rounded button

    public RoundedButton(String text, Color c){
        super(text);
        color = c;
        setForeground(ThemeColors.PRIMARY);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
    }

    // custom paint to draw a rounded rectangle button
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.fillRoundRect(0,0,getWidth(),getHeight(),30,30);
        super.paintComponent(g);
    }
}
