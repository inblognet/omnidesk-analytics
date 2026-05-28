package com.omnidesk.utils;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

public class NotificationUtil {

    public static void sendWarningNotification(String appName, int limitMinutes) {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();
            // Create a transparent/empty image for the tray icon
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "OmniDesk Tracker");
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);

            trayIcon.displayMessage(
                "Time Limit Reached!",
                "You have exceeded your " + limitMinutes + " minute limit on " + appName + ". Time to focus!",
                MessageType.WARNING
            );

            // Remove icon after displaying so it doesn't clutter the taskbar
            tray.remove(trayIcon);

        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
    }
}