import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    public static final int SP = 0;
    public static final int MP = 1;
    static MenuUI menuUI;
    static GameUI active;

    static boolean darkMode;

    public static void main(String[] args) {
        //creating the menu

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        menuUI = new MenuUI("Sıkıysa Yakala V2");
        menuUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuUI.setLocationRelativeTo(null);
        menuUI.setVisible(true);
    }

    public static void startGame(int status) {
        active = new GameUI(status);
        active.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        active.setLocationRelativeTo(null);
        active.setVisible(true);

        menuUI.dispose();

        active.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                menuUI.setVisible(true);
            }
        });
    }
}
