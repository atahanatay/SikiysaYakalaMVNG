import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GamePanel extends JPanel {
    static int win = 0;
    List<String> keys = new ArrayList<>();
    List<Circle> circles = new ArrayList<>();
    JFrame host;
    JLabel info = new JLabel("", SwingConstants.CENTER);
    RedCube redCube = new RedCube(new Grids(2, 3));
    BlueCube blueCube = new BlueCube(new Grids(4, 3));
    boolean labelsCreated = false;
    int status;
    int w, h;
    int turn = 0;
    int blackRepeat = 0, waitingFor = 5, yellowRepeat = 0, yWaitingFor = 5;
    int _team;
    boolean isRedYellow = false;
    boolean canGoBlue = false;

    int p1t = 90, p2t = 90;
    boolean timeStarted;

    Grids redOldPos;

    Circle c1 = new Circle(1, 1), c2 = new Circle(5, 1), c3 = new Circle(1, 5), c4 = new Circle(5, 5);

    KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (turn != 4) {
                if (keys.size() < 5) {
                    if (Character.toString(e.getKeyChar()).matches("[1-5]") && keys.size() != 2) {
                        keys.add(Character.toString(e.getKeyChar()));
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER && keys.size() == 2) {
                        Cube c = getCube(new Grids(Integer.parseInt(keys.get(0)), Integer.parseInt(keys.get(1))));

                        if (c == null || c instanceof GreenCube || c.selectable == 0 || c.locked) {
                            removeSelected();
                            keys.clear();
                        } else {
                            c.select(true);
                            keys.add("-");
                        }
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Cube c = getCube(new Grids(Integer.parseInt(keys.get(3)), Integer.parseInt(keys.get(4))));

                    if (c instanceof GreenCube) {
                        c.greenSelect();
                        if (!isRedYellow) keys.add("-");
                    } else if (redCube.selected && c instanceof BlueCube) {
                        if (!isRedYellow) isRedYellow = true;

                        if (Main.active.panel.isRedYellow && Main.active.panel.canGoBlue) blueCube.rToB();

                        Main.active.panel.repaint();
                    } else {
                        keys.remove(2);
                        keys.remove(3);
                    }
                } else if ((keys.size() == 0 || keys.size() > 5) && Character.toString(e.getKeyChar()).matches("[1-5]")) {
                    keys.clear();
                    keys.add(Character.toString(e.getKeyChar()));
                }

                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && keys.size() > 0)
                    if (keys.size() < 6) {
                        if (!isRedYellow || keys.size() > 3) keys.remove(keys.size() - 1);
                    } else keys.clear();

                refreshText();
            }
        }
    };

    GamePanel(int status, JFrame host) {
        Cube.selectedCube = null;
        GreenCube.greenCubes = new ArrayList<>();

        win = 0;

        if (Main.darkMode) setBackground(Color.black);

        this.status = status;
        this.host = host;

        for (KeyListener keyListener : host.getKeyListeners()) host.removeKeyListener(keyListener);
        host.addKeyListener(keyAdapter);

        setLayout(null);
        setPreferredSize(new Dimension(600, 600));

        add(redCube);
        add(blueCube);

        add(new BlackCube(new Grids(3, 1)));
        add(new BlackCube(new Grids(3, 5)));
        add(new BlackCube(new Grids(1, 2)));
        add(new BlackCube(new Grids(1, 4)));
        add(new BlackCube(new Grids(5, 2)));
        add(new BlackCube(new Grids(5, 4)));

        info.setFont(new Font(Font.MONOSPACED, Font.BOLD, info.getFont().getSize()));
        add(info);

        info.setOpaque(true);
        info.setBackground(getBackground());

        setTurn(turn);

        circles.add(c1);
        circles.add(c2);
        circles.add(c3);
        circles.add(c4);

        for (Circle c : circles) add(c);

        refreshText();
    }

    void winColor() {
        timeStarted = false;

        new Thread(() -> {
            while (Main.active.isVisible()) {
                try {
                    Cube.winColor = Color.YELLOW;
                    repaint();
                    TimeUnit.MILLISECONDS.sleep(500);

                    Cube.winColor = Color.GREEN;
                    repaint();
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void checkForWins() {
        if (!isRedYellow) {
            if (!redCube.checkAround()) {
                win = 1;
                setTurn(4);
                winColor();
            } else if (circles.stream().filter(c -> c.isOpen).count() >= 3) {
                win = 2;
                setTurn(4);
                winColor();
            }
        }
    }

    void time() {
        while (timeStarted) {
            if (turn == 1) p2t--;
            else p1t--;

            refreshText();

            if (p1t == 0 || p2t == 0) {
                removeSelected();
                Cube.removeGreens();
            }

            if (p1t == 0) {
                win = 1;
                setTurn(4);
                winColor();
                timeStarted = false;
            } else if (p2t == 0) {
                win = 2;
                setTurn(4);
                winColor();
                timeStarted = false;
            } else {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void refreshText() {
        if (turn == 1) info.setForeground(Main.darkMode ? Color.LIGHT_GRAY : Color.BLACK);
        else info.setForeground(Main.darkMode ? Main.darkerRed : Color.RED);

        String text = "";
        String time = "";


        if (turn == 4) text = DT.getText(".game-gameover");
        else if (keys.size() == 0) text = DT.getText(".game-enteranumber");

        if (keys.size() > 0) text += keys.get(0) + "x";
        if (keys.size() > 1) text += keys.get(1);
        if (keys.size() > 2) text += " -> ";
        if (keys.size() > 3) text += keys.get(3) + "x";
        if (keys.size() > 4) text += keys.get(4);
        if (keys.size() > 5) text += "*";

        if (keys.size() == 3) text += " ... ";

        time += "P1: " + toMS(p1t) + " - P2: " + toMS(p2t);

        if (isRedYellow) info.setText(text + " - " + yellowRepeat + "/" + yWaitingFor + " - " + time);
        else info.setText(text + " - " + blackRepeat + "/" + waitingFor + " - " + time);
    }

    String toMS(int i) {
        int m = i / 60;
        int s = i % 60;

        return wZero(m) + ":" + wZero(s);
    }

    String wZero(int i) {
        if (Integer.toString(i).length() == 1) return "0" + i;
        return Integer.toString(i);
    }

    void removeSelected() {
        Arrays.stream(getComponents()).filter(c -> c instanceof Cube).forEach(c -> ((Cube) c).selected = false);

        Cube.removeGreens();
    }

    void setTurn(int turn) {
        /*
        0: p1
        1: p2
        2: p1
        3: p1
         */

        this.turn = turn;

        Arrays.stream(getComponents()).filter(c -> c instanceof Cube).forEach(c -> ((Cube) c).selectable = 0);

        if (turn == 0 && (status != Main.MP || _team == 0)) {
            redCube.selectable = 1;
            blueCube.selectable = 1;
        } else if (turn == 1 && (status != Main.MP || _team == 1)) {
            Arrays.stream(getComponents()).filter(component -> component instanceof BlackCube).forEach(component -> ((Cube) component).selectable = 1);
        } else if (turn == 2 && (status != Main.MP || _team == 0)) {
            Arrays.stream(getComponents()).filter(component -> component instanceof BlackCube).forEach(component -> ((Cube) component).selectable = 2);
        } else if (turn == 3 && (status != Main.MP || _team == 0)) {
            Main.active.panel.canGoBlue = false;

            if (yellowRepeat == waitingFor) {
                redCube.og = redOldPos;
                isRedYellow = false;
                yellowRepeat = 0;
                redCube.innerColor = Main.darkMode ? Main.darkerRed : Color.RED;

                keys.clear();
                refreshText();

                setTurn(1);
            } else {
                redCube.selectable = 1;
                removeSelected();
                redCube.select(false);
            }
        }
        if (turn == 4) {
            keys.clear();
            refreshText();
        }

        repaint();
    }

    int nextTurn() {
        if (turn == 3) return 3;
        return (blackRepeat == waitingFor) ? 2 : ((turn == 1) ? 0 : 1);
    }

    Cube getCube(Grids g) {
        return (Cube) Arrays.stream(getComponents())
                .filter(c -> c instanceof Cube && Grids.isEqual(g, ((Cube) c).og))
                .findFirst()
                .orElse(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        w = getWidth() / 5;
        h = (getHeight() - 25) / 5;

        c1.setLocation(5, 5 + 25);
        c2.setLocation(getWidth() - c2.getWidth() - 5, 5 + 25);
        c3.setLocation(5, getHeight() - c3.getHeight() - 5);
        c4.setLocation(getWidth() - c4.getWidth() - 5, getHeight() - c4.getHeight() - 5);

        if (Main.darkMode) g.setColor(Color.LIGHT_GRAY);

        for (int i = 1; i <= 4; i++) {
            g.drawLine(i * w, 25, i * w, getHeight());
            g.drawLine(0, i * h + 25, getWidth(), i * h + 25);
        }

        if (!labelsCreated) {
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    GridLabel l = new GridLabel((x + 1) + "x" + (y + 1), new Grids(x, y));
                    add(l);
                }
            }
        }

        labelsCreated = true;

        for (Component component : getComponents()) {
            if (component instanceof Cube) {
                Cube c = (Cube) component;

                int sw = w / 3;
                int sh = h / 3;

                component.setSize(sw, sh);
                component.setLocation((c.og.x - 1) * w + (w / 2 - sw / 2), (c.og.y - 1) * h + (h / 2 - sh / 2) + 25);
            } else if (component instanceof GridLabel) ((GridLabel) component).set();
        }

//        info.setLocation(0, getHeight() - 25);
        info.setSize(getWidth(), 25);

        revalidate();
    }

    void sendPackage() {
        //for multiplayer send
        //black repeat, cube locations...
    }
}
