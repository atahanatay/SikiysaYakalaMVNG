import javax.swing.*;
import java.awt.*;

public class GridLabel extends JLabel {
    Grids g;

    public GridLabel(String text, Grids g) {
        super(text);
        this.g = g;

        setFont(new Font("monospaced", Font.BOLD, 20));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.BOTTOM);

        set();
    }

    void set() {
        setSize(Main.active.panel.w / 2, 50);
        setLocation(Main.active.panel.w / 4 + g.x * Main.active.panel.w, (g.y + 1) * Main.active.panel.h - getHeight() + 25);
        setForeground(Color.GRAY);
    }
}
