package ua.ie23s.app.felix;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Felix {
    private static TrayIcon trayIcon = null;

    private static boolean isOn = false;

    //start of main method
    public static void main(String[] args) {

        Key key = new Key();

        //checking for support
        if (!SystemTray.isSupported()) {
            System.out.println("System tray is not supported !!! ");
            return;
        }
        //get the systemTray of the system
        SystemTray systemTray = SystemTray.getSystemTray();

        //get default toolkit
        //Toolkit toolkit = Toolkit.getDefaultToolkit();
        //get image
        //Toolkit.getDefaultToolkit().getImage("src/resources/busylogo.jpg");

        //popupmenu
        PopupMenu trayPopupMenu = new PopupMenu();

        //2nd menuitem of popupmenu
        MenuItem close = new MenuItem("Close");
        close.addActionListener(e -> System.exit(0));
        trayPopupMenu.add(close);

        //setting tray icon
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("no.png")), "SystemTray Demo", trayPopupMenu);
        //adjust to default size as per system recommendation
        trayIcon.setImageAutoSize(true);

        trayIcon.addActionListener(actionEvent -> {
            if (isOn) {
                off();
            } else {
                on();
            }

            try {
                URL url = new URL("https://ie23s.site/lamp/change.php");
                URLConnection con = url.openConnection();

                InputStream in = con.getInputStream();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            systemTray.add(trayIcon);
        } catch (AWTException awtException) {
            awtException.printStackTrace();
        }

        new Thread(Felix::get).start();
        System.out.println("end of main");

    }//end of main

    private static void off() {
        trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("off.png")));
        isOn = false;

    }

    private static void on() {
        trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("on.png")));
        isOn = true;

    }

    private static void get() {
        try {
            URL url = new URL("https://ie23s.site/lamp/longpoll.php");
            URLConnection con = url.openConnection();

            BufferedReader inp = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = inp.readLine()) != null) {
                if (inputLine.length() > 0)
                    if (inputLine.contains("0")) {
                        off();
                    } else if (inputLine.contains("1")) {
                        on();
                    }

            }
        } catch (IOException e) {
            trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(
                    ClassLoader.getSystemResource("no.png")));
            get();
        }

    }
}
