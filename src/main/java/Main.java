import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.swing.GroupLayout.DEFAULT_SIZE;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

public class Main {
    public static final int SP = 0;
    public static final int MP = 1;
    public static final Color darkerBlue = Color.decode("#000091");
    public static final Color darkerRed = Color.decode("#910000");
    public static final Color darkerGreen = Color.decode("#009100");
    public static final Color darkerOrange = Color.decode("#00918c");
    public static final Color darkerYellow = Color.YELLOW.darker();
    static MenuUI menuUI;
    static GameUI active;
    static boolean darkMode;

    static WindowAdapter exitAdapter = new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
            menuUI.setVisible(true);
        }
    };

    public static void main(String[] args) throws FileNotFoundException {
        //creating the menu

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String l = JOptionPane.showInputDialog("Language:");
        if (l == null) System.exit(0);
        DT.analyzeAll(l);

        if (DT.searchForOverrides()) {
            String lf = JOptionPane.showInputDialog("Using Overrides (-):");

            List<String> lst = Arrays.stream(lf.split(" ")).map(s -> s.substring(s.indexOf("-") + 1)).collect(Collectors.toList());
            Collections.reverse(lst);

            if (lf != null && !lf.isEmpty()) DT.installOverrides(lst.toArray(new String[0]));
        }

        menuUI = new MenuUI(DT.getText(".menu-title"));
        menuUI.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        menuUI.setLocationRelativeTo(null);
        menuUI.setVisible(true);
    }

    public static void startGame(int status) {
        menuUI.dispose();

        if (status == MP) {
            GridBagConstraints c = new GridBagConstraints();

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
                connect(name.getText());
            });

            host.addActionListener(e -> host());
        } else startSc(status);
    }

    private static void host() {
        try {
            msgBox("Hello", "<html><div style='text-align: center'><a>Bilgisayarların Bağlanması Bekleniyor</a><br><a>Ip Adresiniz: " + InetAddress.getLocalHost().getHostAddress() + "</a></div></html>");

            ServerSocket s = new ServerSocket(42901, 0, InetAddress.getLocalHost());
            System.out.println(s.isClosed());
            System.out.println(s.getInetAddress());
            //s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isConnectable(InetAddress d) {
        try {
            try (Socket s = new Socket(InetAddress.getLocalHost(), 42901)) {
                s.connect(new InetSocketAddress(d, 42901), 5000);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }
    }

    private static void connect(String ip) {
        JDialog d = msgBox("Çok Oyunculu", "Ip adresi test ediliyor...");

        SwingUtilities.invokeLater(() -> {
            try {
                InetAddress address = InetAddress.getByName(ip);

                //Socket s = new Socket(ip, 42901);

                System.out.println(InetAddress.getByName(ip));

                if (isConnectable(address)) {
                    d.dispose();

                    String s = JOptionPane.showInputDialog(null, "Lütfen isminizi girin", "Çok Oyunculu", JOptionPane.PLAIN_MESSAGE);
                    int i = JOptionPane.showConfirmDialog(null, s + " ismiyle,\n" + ip + " ip adresine sahip bilgisayara oyun isteği göndereceksiniz.\nDevam etmek istiyor musunuz?", "Çok Oyunculu", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                } else {
                    d.dispose();

                    JOptionPane.showMessageDialog(null, "Ip adresine bağlanılamıyor", "Çok Oyunculu", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    private static JDialog msgBox(String a, String b) {
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

    private static void startSc(int status) {
        active = new GameUI(status);
        active.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        active.setLocationRelativeTo(null);
        active.setVisible(true);

        active.addWindowListener(exitAdapter);
    }
}
