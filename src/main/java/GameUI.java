import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GameUI extends JFrame {
    GamePanel panel;

    GameUI(int status) {
        setTitle(status == Main.SP ? "Sıkıysa Yakala - Tek Oyunculu" : "Sıkıysa Yakala - Çok Oyunculu");
        setContentPane(panel = new GamePanel(status, this));

        JMenuBar menuBar = new JMenuBar();

        JMenu aboutMenu = new JMenu("Hakkında");
        JMenu settingsMenu = new JMenu("Ayarlar");

        JMenuItem howToPlay = new JMenuItem("Nasıl Oynanır");
        howToPlay.addActionListener(e -> createDialog("Nasıl Oynanır", "howtoplay.html"));

        JMenuItem aboutGame = new JMenuItem("Oyun Hakkında");
        aboutGame.addActionListener(e -> createDialog("Oyun Hakkında", "about.html"));

        JMenuItem blackLimit = new JMenuItem("Siyah hareket limitini ayarla...");

        aboutMenu.add(howToPlay);
        aboutMenu.add(aboutGame);
        menuBar.add(aboutMenu);
        settingsMenu.add(blackLimit);
        menuBar.add(settingsMenu);
        setJMenuBar(menuBar);

        pack();
    }

    private void createDialog(String title, String filename) {
        try {
            JTextPane editorPane = new JTextPane();
            JScrollPane pane = new JScrollPane(editorPane);
            JDialog dialog = new JDialog((Frame) null);

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
