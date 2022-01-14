import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class Main {
    public static final int SP = 0;
    public static final int MP = 1;
    public static Color blue;
    public static Color red;
    public static Color green;
    public static Color orange;
    public static Color yellow;
    public static Color black;
    public static Color gray;

    public static volatile boolean turnMenu = true;

    static MenuUI menuUI;
    static GameUI active;

    static volatile ArrayList<String> msgList = new ArrayList<>();

    static JDialog jdt = null;
    static JTextArea jdt_log = null;

    static WindowAdapter exitAdapter = new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
            if (turnMenu) {
                System.out.println(e.getWindow());
                menuUI.setVisible(true);
                System.out.println("hey");
            } else turnMenu = true;
        }
    };

    static void declareColors() {
        blue = Color.decode(DT.getGlobal(".colorblue"));
        red = Color.decode(DT.getGlobal(".colorred"));
        green = Color.decode(DT.getGlobal(".colorgreen"));
        orange = Color.decode(DT.getGlobal(".colororange"));
        yellow = Color.decode(DT.getGlobal(".coloryellow"));
        black = Color.decode(DT.getGlobal(".colorblack"));
        gray = Color.decode(DT.getGlobal(".colorgray"));
    }

    public static void main(String[] args) throws FileNotFoundException {
        //creating the menu

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String l = JOptionPane.showInputDialog("Language:", "en");
        if (l == null) System.exit(0);
        DT.analyzeAll(l);

        DT.searchForOverrides();

        String lf = JOptionPane.showInputDialog("Using Overrides (-):");

        List<String> lst = Arrays.stream(lf.split(" ")).map(s -> s.substring(s.indexOf("-") + 1)).collect(Collectors.toList());
        Collections.reverse(lst);

        if (!lf.isEmpty()) DT.installOverrides(lst.toArray(new String[0]));

        declareColors();

        if (DT.getConfig(".loglastmsg")) {
            jdt_log = new JTextArea();
            jdt_log.setEditable(false);

            JScrollPane __log = new JScrollPane(jdt_log);

            jdt = new JDialog((Frame) null, "_MP LOG");
            jdt.setVisible(true);
            jdt.add(__log, BorderLayout.CENTER);
            jdt.setPreferredSize(new Dimension(400, 700));
            jdt.pack();
            jdt.setVisible(true);

            mpLogRefresh();
        }

        menuUI = new MenuUI(DT.getText(".menu-title"));
        menuUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuUI.setLocationRelativeTo(null);
        menuUI.setVisible(true);
    }

    public static void mpLogRefresh() {
        if (jdt != null) {
            StringBuilder text = new StringBuilder("LOGGING MP...\n");

            for (String s : msgList) {
                text.append(s).append("\n");
            }

            jdt_log.setText(text.toString());
        }
    }

    public static void startGame(int status) {
        menuUI.dispose();

        if (status == MP) {
            JFrame lookUpUI = new JFrame(DT.getText(".mp-searchcomputer"));
            JPanel lookUpUIPanel = new JPanel();

            JTextField name = new JTextField();

            JButton con = new JButton(DT.getText(".mp-connect"));
            JButton host = new JButton(DT.getText(".mp-host"));

            GroupLayout layout = new GroupLayout(lookUpUIPanel);

            lookUpUI.setResizable(false);

            layout.setHorizontalGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                            .addGroup(layout.createSequentialGroup()
                                    .addComponent(name, 250, 250, Short.MAX_VALUE)
                                    .addGap(5)
                                    .addComponent(con, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                            )
                            .addComponent(host, 0, DEFAULT_SIZE, Short.MAX_VALUE)
                    )
                    .addContainerGap()
            );

            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup()
                            .addComponent(name, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                            .addComponent(con, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    )
                    .addGap(5)
                    .addComponent(host, PREFERRED_SIZE, DEFAULT_SIZE, PREFERRED_SIZE)
                    .addContainerGap()
            );

            layout.linkSize(SwingUtilities.VERTICAL, name, con);

            lookUpUIPanel.setLayout(layout);
            lookUpUI.setContentPane(lookUpUIPanel);

            lookUpUI.pack();
            lookUpUI.setLocationRelativeTo(null);
            lookUpUI.setVisible(true);
            lookUpUI.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            lookUpUI.addWindowListener(exitAdapter);

            con.addActionListener(e -> {
                //ipPane.add(new ComputerPanel("Connecting1", "192.168.1.1"));
                connect(name.getText(), lookUpUI);
            });

            host.addActionListener(e -> NetworkConnection.setupAsHost(lookUpUI));
        } else startSc(SP, 0);
    }

    private static void connect(String ip, JFrame lookUpUI) {
        JDialog d = msgBox("Çok Oyunculu", "Ip adresi test ediliyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                if (NetworkConnection.setupAsReceiver(ip)) {
                    turnMenu = false;
                    lookUpUI.dispose();

                    startSc(MP, 0);
                }

                d.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    public static JDialog msgBox(String a, String b) {
        JDialog d = new JDialog((Frame) null, a);

        d.setPreferredSize(new Dimension(300, 100));

        d.setLayout(new BorderLayout());
        d.add(new JLabel(b, SwingConstants.CENTER), BorderLayout.CENTER);

        d.pack();
        d.setLocationRelativeTo(null);
        d.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        d.setVisible(true);

        return d;
    }

    public static void startSc(int status, int team) {
        active = new GameUI(status, team);
        active.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        active.setLocationRelativeTo(null);
        active.setVisible(true);

        active.addWindowListener(exitAdapter);
    }
}
