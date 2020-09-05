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

        JMenuItem howToPlay = new JMenuItem("Nasıl Oynanır");
        howToPlay.addActionListener(e -> createDialog("Nasıl Oynanır", "howtoplay.html"));

        JMenuItem aboutGame = new JMenuItem("Oyun Hakkında");
        aboutGame.addActionListener(e -> createDialog("Oyun Hakkında", "about.html"));

        aboutMenu.add(howToPlay);
        aboutMenu.add(aboutGame);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);

        pack();
    }

    private void createDialog(String title, String filename) {
        try {
            JEditorPane editorPane = new JEditorPane(getClass().getResource(filename));
            JScrollPane pane = new JScrollPane(editorPane);
            JDialog dialog = new JDialog((Frame) null);
            editorPane.setContentType("text/html");
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
