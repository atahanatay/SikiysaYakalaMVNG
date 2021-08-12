import javax.swing.*;
import java.awt.*;

public class Circle extends JPanel {
    boolean isOpen = false;
    Grids g;

    public Circle(int x, int y) {
        this.g = new Grids(x, y);
        setSize(10, 10);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(isOpen ? Main.green : Main.red);
        g.fillOval(0, 0, getWidth(), getHeight());
    }
}
