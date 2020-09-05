import javax.swing.*;
import java.awt.*;

public class Circle extends JPanel {
    boolean isOpen = false;

    public Circle() {
        setSize(10, 10);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(isOpen ? Color.GREEN : Color.RED);
        g.fillOval(0, 0, getWidth(), getHeight());
    }
}
