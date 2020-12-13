import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Cube extends JPanel {
    public static final int a = 0, b = 1;
    static Cube selectedCube;
    static Color winColor;
    Grids g;
    Color innerColor;
    Color outerColor;
    int selectable = 0;
    boolean selected = false, locked = false;

    Cube(Grids g, Color innerColor) {
        this.g = g;
        this.innerColor = innerColor;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (Cube.this instanceof GreenCube) {
                    greenSelect();
                } else {
                    select(false);
                }
            }
        });

        repaint();
    }

    static void removeGreens() {
        for (GreenCube greenCube : GreenCube.greenCubes) {
            Main.active.panel.remove(greenCube);
        }

        GreenCube.greenCubes.clear();
        Main.active.panel.revalidate();
        Main.active.panel.repaint();
    }

    void greenSelect() {
        if (selectedCube.selectable == 2) {
            selectedCube.locked = true;
        } else if (selectedCube instanceof BlackCube) {
            for (Component component : Main.active.panel.getComponents()) {
                if (component instanceof BlackCube) ((BlackCube) component).locked = false;
            }
        }

        if (selectedCube.selectable == 2) Main.active.panel.blackRepeat = 0;
        else if (selectedCube instanceof BlackCube) Main.active.panel.blackRepeat++;

        selectedCube.g = g;

        if (selectedCube instanceof RedCube) {
            for (Circle c : Main.active.panel.circles) {
                if (c.g.x == selectedCube.g.x && c.g.y == selectedCube.g.y) c.isOpen = true;
            }
        }

        Main.active.panel.removeSelected();
        Main.active.panel.setTurn(Main.active.panel.nextTurn());
        removeGreens();

        String[] keys = {Integer.toString(g.x), Integer.toString(g.y), "-"};
        IntStream.rangeClosed(3, 5).forEach(i -> {
            if (Main.active.panel.keys.size() > i) Main.active.panel.keys.set(i, keys[i - 3]);
            else Main.active.panel.keys.add(keys[i - 3]);
        });

        Main.active.panel.checkForWins();
        Main.active.panel.refreshText();
    }

    void select(boolean viaKey) {
        if (selectable >= 1 && !locked) {
            removeGreens();

            for (Component component : Main.active.panel.getComponents()) {
                if (component instanceof Cube) ((Cube) component).selected = false;
            }
            selected = true;
            selectedCube = Cube.this;

            if (Cube.this instanceof BlueCube || Cube.this instanceof BlackCube) moveCubes(a);
            else moveCubes(b);

            if (!viaKey)
                Main.active.panel.keys = new ArrayList<>(Arrays.asList(Integer.toString(g.x), Integer.toString(g.y), "-"));
            Main.active.panel.refreshText();

            Main.active.panel.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this instanceof GreenCube) {
            g.setColor(Main.darkMode ? Color.decode("#009100") : Color.GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            if (selectable == 1) outerColor = Main.darkMode ? Color.decode("#00918c") : Color.ORANGE;
            else if (selectable == 2) outerColor = Main.darkMode ? Color.decode("#000091") : Color.BLUE;
            else outerColor = innerColor;

            if (locked) outerColor = Main.darkMode ? Color.decode("#910000") : Color.RED;
            if (selected) outerColor = Main.darkMode ? Color.decode("#009100") : Color.GREEN;

            if ((GamePanel.win == 1 && this instanceof BlackCube) ||
                    (GamePanel.win == 2 && (this instanceof RedCube || this instanceof BlueCube)))
                outerColor = winColor;

            g.setColor(outerColor);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(innerColor);
            g.fillRect(5, 5, getWidth() - 10, getHeight() - 10);
        }
    }

    boolean isAllTrue(boolean[] x) {
        for (boolean b : x) if (!b) return false;
        return true;
    }

    boolean checkGrid(Grids g) {
        return Main.active.panel.getCube(g) == null && g.x <= 5 && g.x > 0 && g.y <= 5 && g.y > 0;
    }

    boolean checkGridA(Grids g) {
        return (Main.active.panel.getCube(g) == null || Main.active.panel.getCube(g) instanceof BlueCube) && g.x <= 5 && g.x > 0 && g.y <= 5 && g.y > 0;
    }

    void moveCubes(int style) {
        int mx = selectedCube.g.x, my = selectedCube.g.y;

        if (style == a) {
            boolean[] directions = new boolean[8];

            for (int i = 1; true; i++) {
                Grids g;

                if (!directions[0] && checkGrid(g = new Grids(mx + i, my + i)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[0] = true;

                if (!directions[1] && checkGrid(g = new Grids(mx + i, my - i)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[1] = true;

                if (!directions[2] && checkGrid(g = new Grids(mx + i, my)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[2] = true;

                if (!directions[3] && checkGrid(g = new Grids(mx - i, my + i)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[3] = true;

                if (!directions[4] && checkGrid(g = new Grids(mx - i, my - i)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[4] = true;

                if (!directions[5] && checkGrid(g = new Grids(mx - i, my)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[5] = true;

                if (!directions[6] && checkGrid(g = new Grids(mx, my + i)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[6] = true;

                if (!directions[7] && checkGrid(g = new Grids(mx, my - i)))
                    Main.active.panel.add(new GreenCube(g));
                else directions[7] = true;

                if (isAllTrue(directions)) break;
            }
        } else if (style == b) {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    Grids g;
                    if (checkGrid(g = new Grids(mx + x, my + y)))
                        Main.active.panel.add(new GreenCube(g));
                }
            }
        }
    }

    boolean checkAround() {
        int mx = g.x, my = g.y;

        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (checkGridA(new Grids(mx + x, my + y)))
                    return true;
            }
        }

        return false;
    }
}

class GreenCube extends Cube {
    static ArrayList<GreenCube> greenCubes = new ArrayList<>();

    GreenCube(Grids g) {
        super(g, Main.darkMode ? Color.decode("#009100") : Color.GREEN);
        greenCubes.add(this);
    }
}

class BlackCube extends Cube {

    BlackCube(Grids g) {
        super(g, Main.darkMode ? Color.LIGHT_GRAY : Color.BLACK);
    }
}

class BlueCube extends Cube {

    BlueCube(Grids g) {
        super(g, Main.darkMode ? Color.decode("#000091") : Color.BLUE);
    }
}

class RedCube extends Cube {

    RedCube(Grids g) {
        super(g, Main.darkMode ? Color.decode("#910000") : Color.RED);
    }
}
