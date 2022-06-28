package ua.ie23s.app.felix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Felix {
    private static TrayIcon trayIcon = null;

    private static boolean isOn = false;
    private static MenuItem action;

    //start of main method
    public static void main(String []args) throws IOException {

        //checking for support
        if(!SystemTray.isSupported()){
            System.out.println("System tray is not supported !!! ");
            return ;
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
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (isOn) {
                    off();
                } else  {
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
            }
        });
        trayPopupMenu.add(action);

        //2nd menuitem of popupmenu
        MenuItem close = new MenuItem("Close");
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        trayPopupMenu.add(close);

        //setting tray icon
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(
                ClassLoader.getSystemResource("no.png")), "SystemTray Demo", trayPopupMenu);
        //adjust to default size as per system recommendation
        trayIcon.setImageAutoSize(true);

        try{
            systemTray.add(trayIcon);
        }catch(AWTException awtException){
            awtException.printStackTrace();
        }

        new Thread(() -> get()).start();
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

                InputStream in = con.getInputStream();
            StringBuilder sb = new StringBuilder();
            BufferedReader inp = new BufferedReader(new InputStreamReader(
                    con.getInputStream()));
            String inputLine;
            while ((inputLine = inp.readLine()) != null) {
                if(inputLine.length() > 0)
                    if(inputLine.contains("0")) {
                        off();
                    } else if(inputLine.contains("1")) {
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
