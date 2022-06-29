package ua.ie23s.app.felix;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.zip.CRC32;

public class Felix {
    private static TrayIcon trayIcon = null;

    private static boolean isOn = false;
    private static MenuItem action;

    //start of main method
    public static void main(String[] args) {

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

        //1t menuitem for popupmenu
        action = new MenuItem("On");
        action.addActionListener(actionEvent -> {
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
        trayPopupMenu.add(action);

        //2nd menuitem of popupmenu
        MenuItem close = new MenuItem("Close");
        close.addActionListener(e -> System.exit(0));
        trayPopupMenu.add(close);

        //setting tray icon
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("no.png")), "SystemTray Demo", trayPopupMenu);
        //adjust to default size as per system recommendation
        trayIcon.setImageAutoSize(true);

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
        action.setLabel("On");
        isOn = false;

    }

    private static void on() {
        trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("on.png")));
        action.setLabel("Off");
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
    private String randomKey() {

        ///this is user supplied string
        String a = "ABCDEFGHI";
        int mySaltSizeInBytes = 32;
        //SecureRandom class provides strong random numbers
        SecureRandom random = new SecureRandom();

        //salt mitigates dictionary/rainbow attacks
        byte[] salt = new byte[mySaltSizeInBytes];

        //random fill salt buffer with random bytes
        random.nextBytes(salt);

        //concatenates string a  and salt
        //into one big bytebuffer ready to be digested
        //this is just one way to do it
        //there might be better ways

        ByteBuffer bbuffer = ByteBuffer.allocate(mySaltSizeInBytes+a.length());
        bbuffer.put(salt);
        bbuffer.put(a.getBytes());

        //your crc class
        CRC32 crc = new CRC32();
        crc.update(bbuffer.array());
        return Long.toHexString(crc.getValue());
    }
}
