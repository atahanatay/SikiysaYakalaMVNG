import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class NetworkConnection {
    static Socket receiver = null;
    static ServerSocket serverSocket = null;

    static PrintWriter writer = null;
    static Scanner scanner = null;

    static File f = new File("./mplog.txt");
    static FileWriter fw = null;

    static void createLog() {
        if (DT.getConfig(".createmplog")) {
            try {
                f.createNewFile();
                fw = new FileWriter(f, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void putLog(String s, int p) {
        if (fw != null) {
            try {
                fw.write(String.format("%d: %s\n", p, s));
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void endLog() {
        try {
            if (fw != null) fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void sendPackage(String pack) {
        writer.println(pack);
        String s = scanner.nextLine();

        putLog(pack, 0);
        putLog(s, 1);
    }

    static String getPackage() {
        String result = scanner.nextLine();
        writer.println("continue");

        putLog(result, 1);
        putLog("ok", 0);

        return result;
    }

    static void createStreams() throws IOException {
        if (writer != null) writer.close();
        if (scanner != null) scanner.close();

        writer = new PrintWriter(receiver.getOutputStream(), true);
        scanner = new Scanner(receiver.getInputStream());
    }

    static boolean isConnectable(String ip) {
        try {
            receiver = new Socket(InetAddress.getByName(ip), 42901);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Main.msgBox("Hata", "Bağlanılamadı");
            return false;
        }
    }

    static void setupAsReceiver(String ip) throws UnknownHostException {
        createLog();
        try {
            if (isConnectable(ip)) {
                String s = JOptionPane.showInputDialog(null, "Lütfen isminizi girin", "Çok Oyunculu", JOptionPane.PLAIN_MESSAGE);
                int i = JOptionPane.showConfirmDialog(null, s + " ismiyle,\n" + ip + " ip adresine sahip bilgisayara oyun isteği göndereceksiniz.\nDevam etmek istiyor musunuz?", "Çok Oyunculu", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

                createStreams();

                sendPackage(i == JOptionPane.YES_OPTION ? "t" : "f");
                sendPackage(s);
            } else {
                JOptionPane.showMessageDialog(null, "Ip adresine bağlanılamıyor", "Çok Oyunculu", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("test");
        endLog();
    }


    static void setupAsHost(JFrame cl) {
        createLog();
        try {
            JDialog d = Main.msgBox("Hello", "<html><div style='text-align: center'><a>Bilgisayarların Bağlanması Bekleniyor</a><br><a>Ip Adresiniz: " + InetAddress.getLocalHost().getHostAddress() + "</a></div></html>");

            SwingUtilities.invokeLater(() -> {
                hostLookup();
                Main.turnMenu = false;
                d.dispose();
                cl.dispose();
                Main.turnMenu = true;
                Main.startSc(Main.MP, 1);
            });
        } catch (IOException e) {
            e.printStackTrace();
            Main.msgBox("Hata", "Bir hata oluştu");
        }
    }

    static void hostLookup() {
        try {
            serverSocket = new ServerSocket(42901, 0, InetAddress.getLocalHost());

            System.out.println(serverSocket.isClosed());
            System.out.println(serverSocket.getInetAddress());

            System.out.println("c1");

            receiver = serverSocket.accept();
            createStreams();

            System.out.println("c3");

            if (getPackage().equals("t")) {
                System.out.println("c4");

                System.out.println(getPackage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        endLog();
    }
}
