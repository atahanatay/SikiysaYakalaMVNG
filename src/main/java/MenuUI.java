import javax.swing.*;
import java.awt.*;

public class MenuUI extends JFrame {
    JLabel logo = new JLabel(DT.getText(".menu-logotext"));

    JButton sp = new JButton(DT.getText(".menu-sp"));
    JButton mp = new JButton(DT.getText(".menu-mp"));
    JButton exit = new JButton(DT.getText(".menu-exit"));

    MenuUI(String title) {
        super(title);

        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setVerticalAlignment(SwingConstants.CENTER);

        logo.setFont(logo.getFont().deriveFont(60f));

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        add(logo, c);
        c.weighty = 0;
        c.insets = new Insets(0, 5, 5, 5);

        c.gridy = 1;
        add(sp, c);
        c.gridy = 2;
        add(mp, c);
        c.gridy = 3;
        add(exit, c);
        c.gridy = 4;

        Dimension d = new Dimension(150, 30);
        sp.setPreferredSize(d);
        mp.setPreferredSize(d);
        exit.setPreferredSize(d);

        setPreferredSize(new Dimension(500, 300));

        exit.addActionListener(e -> System.exit(0));
        sp.addActionListener(e -> Main.startGame(Main.SP));
        mp.addActionListener(e -> Main.startGame(Main.MP));

        pack();
    }
}
