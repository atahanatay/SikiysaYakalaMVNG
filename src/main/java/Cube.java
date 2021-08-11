import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import static java.awt.event.MouseEvent.BUTTON1;

public class Cube extends JPanel {
    public static final int a = 0, b = 1;
    static Cube selectedCube;
    static Color winColor;
    Grids og;
    Color innerColor;
    Color outerColor;
    int selectable = 0;
    boolean selected = false, locked = false;

    Cube(Grids g, Color innerColor) {
        this.og = g;
        this.innerColor = innerColor;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (Cube.this instanceof GreenCube && e.getButton() == BUTTON1) {
                    greenSelect();
                } else if (!Main.active.panel.isRedYellow && Cube.this instanceof BlueCube && selectedCube instanceof RedCube && (e.isControlDown() || e.getButton() == MouseEvent.BUTTON3)) {
                    Main.active.panel.isRedYellow = true;
                    Main.active.panel.repaint();
                } else if (Main.active.panel.isRedYellow && Cube.this instanceof BlueCube && selectedCube instanceof RedCube && Main.active.panel.canGoBlue) {
                    rToB();
                } else {
                    select(false);
                }
            }
        });

        repaint();
    }

    void rToB() {
        Main.active.panel.removeSelected();
        removeGreens();

        Main.active.panel.isRedYellow = false;
        Main.active.panel.yellowRepeat = 0;
        Main.active.panel.redCube.innerColor = Main.darkMode ? Main.darkerRed : Color.RED;

        Main.active.panel.redCube.og = og;
        og = Main.active.panel.redOldPos;

        Main.active.panel.yellowRepeat = Main.active.panel.yWaitingFor;

        Main.active.panel.keys = new ArrayList<>(Arrays.asList(Integer.toString(og.x), Integer.toString(og.y), "-", Integer.toString(selectedCube.og.x), Integer.toString(selectedCube.og.y), "-"));
        Main.active.panel.setTurn(1);

        Main.active.panel.refreshText();
        Main.active.panel.repaint();

        checkForCircles();
    }

    private void checkForCircles() {
        if (selectedCube instanceof RedCube && !Main.active.panel.isRedYellow) {
            for (Circle c : Main.active.panel.circles) {
                if (c.g.x == selectedCube.og.x && c.g.y == selectedCube.og.y) c.isOpen = true;
            }
        }
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

        if (Main.active.panel.isRedYellow) Main.active.panel.yellowRepeat++;

        selectedCube.og = og;

        checkForCircles();

        String[] keys = {Integer.toString(og.x), Integer.toString(og.y), "-"};
        IntStream.rangeClosed(3, 5).forEach(i -> {
            if (Main.active.panel.keys.size() > i) Main.active.panel.keys.set(i, keys[i - 3]);
            else
                Main.active.panel.keys.add(keys[i - 3]);
        });

        if (Main.active.panel.isRedYellow)
            Main.active.panel.keys.clear();

        Main.active.panel.removeSelected();
        removeGreens();
        Main.active.panel.setTurn(Main.active.panel.nextTurn());

        Main.active.panel.checkForWins();
        Main.active.panel.refreshText();
    }

    void select(boolean viaKey) {
        if (selectable >= 1 && !locked) {
            if (!Main.active.panel.timeStarted) {
                Main.active.panel.timeStarted = true;
                new Thread(Main.active.panel::time).start();
            }

            removeGreens();

            for (Component component : Main.active.panel.getComponents()) {
                if (component instanceof Cube) ((Cube) component).selected = false;
            }
            selected = true;
            selectedCube = Cube.this;

            if (Cube.this instanceof BlueCube || Cube.this instanceof BlackCube || (Cube.this instanceof RedCube && Main.active.panel.isRedYellow))
                moveCubes(a);
            else moveCubes(b);

            if (!viaKey)
                Main.active.panel.keys = new ArrayList<>(Arrays.asList(Integer.toString(og.x), Integer.toString(og.y), "-"));
            Main.active.panel.refreshText();

            Main.active.panel.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this instanceof GreenCube) {
            g.setColor(Main.darkMode ? Main.darkerGreen : Color.GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            if (selectable == 1) outerColor = Main.darkMode ? Main.darkerOrange : Color.ORANGE;
            else if (selectable == 2) outerColor = Main.darkMode ? Main.darkerBlue : Color.BLUE;
            else outerColor = innerColor;

            if (locked) outerColor = Main.darkMode ? Main.darkerRed : Color.RED;
            if (selected) outerColor = Main.darkMode ? Main.darkerGreen : Color.GREEN;

            if ((GamePanel.win == 1 && this instanceof BlackCube) ||
                    (GamePanel.win == 2 && (this instanceof RedCube || this instanceof BlueCube)))
                outerColor = winColor;

            if (this instanceof RedCube && Main.active.panel.isRedYellow && Main.active.panel.turn != 3) {
                outerColor = Main.darkMode ? Main.darkerYellow : Color.YELLOW;
                innerColor = Main.darkMode ? Main.darkerYellow : Color.YELLOW;
                Main.active.panel.setTurn(3);
                Main.active.panel.redOldPos = this.og;
            }

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
        if (Main.active.panel.getCube(g) instanceof BlueCube) {
            Main.active.panel.canGoBlue = true;
        }

        return Main.active.panel.getCube(g) == null && g.x <= 5 && g.x > 0 && g.y <= 5 && g.y > 0;
    }

    boolean checkGridA(Grids g) {
        return Main.active.panel.getCube(g) == null && g.x <= 5 && g.x > 0 && g.y <= 5 && g.y > 0;
    }

    void moveCubes(int style) {
        int mx = selectedCube.og.x, my = selectedCube.og.y;

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
        int mx = og.x, my = og.y;

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
    static ArrayList<GreenCube> greenCubes;

    GreenCube(Grids g) {
        super(g, Main.darkMode ? Main.darkerGreen : Color.GREEN);
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
        super(g, Main.darkMode ? Main.darkerBlue : Color.BLUE);
    }
}

class RedCube extends Cube {
    RedCube(Grids g) {
        super(g, Main.darkMode ? Main.darkerRed : Color.RED);
    }
}
