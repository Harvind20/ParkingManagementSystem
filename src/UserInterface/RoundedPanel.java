package UserInterface;
import java.awt.*;
import javax.swing.*;

public class RoundedPanel extends JPanel {
    int r;

    public RoundedPanel(int r){
        this.r = r;
        setOpaque(false);
    }

    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(getBackground());
        g2.fillRoundRect(0,0,getWidth(),getHeight(),r,r);
        super.paintComponent(g2);
        
    }
}