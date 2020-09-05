import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamePanel extends JPanel {
    List<String> keys = new ArrayList<>();
    int status;
    JFrame host;
    boolean labelsCreated = false;
    JLabel info = new JLabel("", SwingConstants.CENTER);
    int w, h;
    int turn = 0;
    RedCube redCube = new RedCube(new Grids(2, 3));
    BlueCube blueCube = new BlueCube(new Grids(4, 3));

    int blackRepeat = 0, waitingFor = 3;
    int _team;

    Circle c1 = new Circle(), c2 = new Circle(), c3 = new Circle(), c4 = new Circle();

    KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (keys.size() < 5) {
                if (Character.toString(e.getKeyChar()).matches("[1-5]") && keys.size() != 2) {
                    keys.add(Character.toString(e.getKeyChar()));
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && keys.size() > 0) {
                    keys.remove(keys.size() - 1);
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
                    keys.add("-");
                } else {
                    keys.remove(3);
                    keys.remove(4);
                }
            } else if (Character.toString(e.getKeyChar()).matches("[1-5]")) {
                keys.clear();
                keys.add(Character.toString(e.getKeyChar()));
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                keys.clear();
            }

            refreshText();
        }
    };

    GamePanel(int status, JFrame host) {
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
        info.setForeground(Color.RED);

        setTurn(turn);

        add(c1);
        add(c2);
        add(c3);
        add(c4);

        refreshText();
    }

    void refreshText() {
        String text = "";

        if (keys.size() == 0) text = "Bir sayı girin";

        if (keys.size() > 0) text += keys.get(0) + "x";
        if (keys.size() > 1) text += keys.get(1);
        if (keys.size() > 2) text += " -> ";
        if (keys.size() > 3) text += keys.get(3) + "x";
        if (keys.size() > 4) text += keys.get(4);
        if (keys.size() > 5) text += " *";

        info.setText(text);
    }

    void removeSelected() {
        for (Component component : getComponents()) {
            if (component instanceof Cube) ((Cube) component).selected = false;
        }

        Cube.removeGreens();
    }

    void setTurn(int turn) {
        this.turn = turn;

        for (Component component : getComponents()) {
            if (component instanceof Cube) ((Cube) component).selectable = 0;
        }

        if (turn == 0 && (status != Main.MP || _team == 0)) {
            redCube.selectable = 1;
            blueCube.selectable = 1;
        } else if (turn == 1 && (status != Main.MP || _team == 1)) {
            Arrays.stream(getComponents()).filter(component -> component instanceof BlackCube).forEach(component -> ((Cube) component).selectable = 1);
        } else if (turn == 2 && (status != Main.MP || _team == 0)) {
            Arrays.stream(getComponents()).filter(component -> component instanceof BlackCube).forEach(component -> ((Cube) component).selectable = 2);
        }

        repaint();
    }

    int nextTurn() {
        return (blackRepeat == waitingFor) ? 2 : ((turn == 0) ? 1 : 0);
    }

    Cube getCube(Grids g) {
        for (Component component : getComponents()) {
            if (component instanceof Cube && Grids.isEqual(g, ((Cube) component).g)) {
                return (Cube) component;
            }
        }

        return null;
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
                component.setLocation((c.g.x - 1) * w + (w / 2 - sw / 2), (c.g.y - 1) * h + (h / 2 - sh / 2) + 25);
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
