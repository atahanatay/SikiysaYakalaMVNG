import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cube extends JPanel {
    public static final int a = 0, b = 1;
    static Cube selectedCube;
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
            public void mouseClicked(MouseEvent e) {
                if (Cube.this instanceof GreenCube) {
                    greenSelect();
                } else {
                    select(false);
                }
            }
        });

        repaint();
    }

    void greenSelect() {
        if (selectedCube.selectable == 2) {
            selectedCube.locked = true;
        } else if (selectedCube instanceof BlackCube) {
            for (Component component : Main.active.panel.getComponents()) {
                if (component instanceof BlackCube) ((BlackCube) component).locked = false;
            }
        }

        if (selectedCube instanceof BlackCube) Main.active.panel.blackRepeat++;

        selectedCube.g = g;
        Main.active.panel.removeSelected();
        Main.active.panel.setTurn(Main.active.panel.nextTurn());
        removeGreens();

        //todo fixhere
        String[] keys = {Integer.toString(g.x), Integer.toString(g.y), "-"};
        IntStream.rangeClosed(3, 5).forEach(i -> {
            if (Main.active.panel.keys.size() > i) Main.active.panel.keys.set(i, keys[i - 3]);
            else Main.active.panel.keys.add(keys[i - 3]);
        });

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

            if (!viaKey) Main.active.panel.keys = new ArrayList<>(Arrays.asList(Integer.toString(g.x), Integer.toString(g.y), "-"));
            Main.active.panel.refreshText();

            Main.active.panel.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (this instanceof GreenCube) {
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            if (selectable == 1) outerColor = Color.ORANGE;
            else if (selectable == 2) outerColor = Color.BLUE;
            else outerColor = innerColor;

            if (locked) outerColor = Color.RED;
            if (selected) outerColor = Color.GREEN;

            g.setColor(outerColor);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(innerColor);
            g.fillRect(5, 5, getWidth() - 10, getHeight() - 10);
        }
    }

    void moveCubes(int style) {
        if (style == a) {
            for (int y = g.y + 1; y <= 5; y++) {
                if (Main.active.panel.getCube(new Grids(g.x, y)) != null) break;

                Main.active.panel.add(new GreenCube(new Grids(g.x, y)));
            }

            for (int y = g.y - 1; y >= 1; y--) {
                if (Main.active.panel.getCube(new Grids(g.x, y)) != null) {
                    break;
                }

                Main.active.panel.add(new GreenCube(new Grids(g.x, y)));
            }

            for (int x = g.x + 1; x <= 5; x++) {
                if (Main.active.panel.getCube(new Grids(x, g.y)) != null) break;

                Main.active.panel.add(new GreenCube(new Grids(x, g.y)));
            }

            for (int x = g.x - 1; x >= 1; x--) {
                if (Main.active.panel.getCube(new Grids(x, g.y)) != null) {
                    break;
                }

                Main.active.panel.add(new GreenCube(new Grids(x, g.y)));
            }

            int x = g.x + 1;
            int y = g.y + 1;
            while (x <= 5 && y <= 5) {
                if (Main.active.panel.getCube(new Grids(x, y)) != null) {
                    break;
                }

                Main.active.panel.add(new GreenCube(new Grids(x, y)));

                x++;
                y++;
            }

            x = g.x + 1;
            y = g.y - 1;
            while (x <= 5 && y >= 1) {
                if (Main.active.panel.getCube(new Grids(x, y)) != null) {
                    break;
                }

                Main.active.panel.add(new GreenCube(new Grids(x, y)));

                x++;
                y--;
            }

            x = g.x - 1;
            y = g.y + 1;
            while (x >= 1 && y <= 5) {
                if (Main.active.panel.getCube(new Grids(x, y)) != null) {
                    break;
                }

                Main.active.panel.add(new GreenCube(new Grids(x, y)));

                x--;
                y++;
            }

            x = g.x - 1;
            y = g.y - 1;
            while (x >= 1 && y >= 1) {
                if (Main.active.panel.getCube(new Grids(x, y)) != null) {
                    break;
                }

                Main.active.panel.add(new GreenCube(new Grids(x, y)));

                x--;
                y--;
            }
        } else if (style == b) {
            if (Main.active.panel.getCube(new Grids(g.x, g.y + 1)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x, g.y + 1)));
            if (Main.active.panel.getCube(new Grids(g.x, g.y - 1)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x, g.y - 1)));
            if (Main.active.panel.getCube(new Grids(g.x + 1, g.y)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x + 1, g.y)));
            if (Main.active.panel.getCube(new Grids(g.x - 1, g.y)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x - 1, g.y)));
            if (Main.active.panel.getCube(new Grids(g.x + 1, g.y + 1)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x + 1, g.y + 1)));
            if (Main.active.panel.getCube(new Grids(g.x - 1, g.y + 1)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x - 1, g.y + 1)));
            if (Main.active.panel.getCube(new Grids(g.x + 1, g.y - 1)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x + 1, g.y - 1)));
            if (Main.active.panel.getCube(new Grids(g.x - 1, g.y - 1)) == null)
                Main.active.panel.add(new GreenCube(new Grids(g.x - 1, g.y - 1)));
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
}

class GreenCube extends Cube {
    static ArrayList<GreenCube> greenCubes = new ArrayList<>();

    GreenCube(Grids g) {
        super(g, Color.GREEN);
        greenCubes.add(this);
    }
}

class BlackCube extends Cube {

    BlackCube(Grids g) {
        super(g, Color.BLACK);
    }
}

class BlueCube extends Cube {

    BlueCube(Grids g) {
        super(g, Color.BLUE);
    }
}

class RedCube extends Cube {

    RedCube(Grids g) {
        super(g, Color.RED);
    }
}
