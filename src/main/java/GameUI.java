import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameUI extends JFrame {
    GamePanel panel;

    GameUI(int status, int team) {
        setTitle(DT.getText(status == Main.SP ? ".gameui-sp" : ".gameui-mp"));
        setContentPane(panel = new GamePanel(status, this, team));

        JMenuBar menuBar = new JMenuBar();

        JMenu aboutMenu = new JMenu(DT.getText(".gameui-about"));
        JMenu settingsMenu = new JMenu(DT.getText(".gameui-settings"));

        JMenuItem howToPlay = new JMenuItem(DT.getText(".gameui-htp"));
        howToPlay.addActionListener(e -> createDialog(DT.getText(".gameui-htp"), "howtoplay.html"));

        JMenuItem aboutGame = new JMenuItem(DT.getText(".gameui-aboutgame"));
        aboutGame.addActionListener(e -> createDialog(DT.getText(".gameui-aboutgame"), "about.html"));

        JMenuItem blackLimit = new JMenuItem(DT.getText(".gameui-sbml"));

        JMenuItem restart = new JMenuItem(DT.getText(".gameui-restart"));
        restart.addActionListener(e -> {
            GameUI.this.dispose();
            SwingUtilities.invokeLater(() -> Main.startGame(status));
        });

        settingsMenu.add(blackLimit);
        settingsMenu.add(restart);
        menuBar.add(settingsMenu);
        aboutMenu.add(howToPlay);
        aboutMenu.add(aboutGame);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);

        pack();
    }

    private void createDialog(String title, String filename) {
        try {
            JTextPane editorPane = new JTextPane();
            JScrollPane pane = new JScrollPane(editorPane);
            JDialog dialog = new JDialog((Frame) null);

            dialog.setPreferredSize(new Dimension(800, 600));

            editorPane.setPage(getClass().getResource(filename));
            editorPane.setEditable(false);

            pane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEtchedBorder()));

            dialog.add(pane);
            dialog.setTitle(title);
            dialog.setVisible(true);
            dialog.pack();
            dialog.setLocationRelativeTo(null);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
