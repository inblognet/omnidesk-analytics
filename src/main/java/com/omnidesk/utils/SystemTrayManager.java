package com.omnidesk.utils;

import com.omnidesk.services.ActivityMonitorService;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SystemTrayManager {

    public static void setupTray(Stage primaryStage, ActivityMonitorService tracker) {
        if (!SystemTray.isSupported()) return;

        SystemTray tray = SystemTray.getSystemTray();

        // Create a simple blue square icon in memory (so we don't need to download an image file)
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(13, 17, 23)); // Dark theme color
        g.fillRect(0, 0, 16, 16);
        g.setColor(new Color(63, 185, 80)); // Green accent
        g.fillRect(4, 4, 8, 8);
        g.dispose();

        TrayIcon trayIcon = new TrayIcon(image, "OmniDesk Analytics");
        trayIcon.setImageAutoSize(true);

        // Double-click the icon to open the app
        trayIcon.addActionListener(e -> Platform.runLater(() -> {
            primaryStage.show();
            primaryStage.setIconified(false);
        }));

        // The Right-Click Menu
        PopupMenu popup = new PopupMenu();

        MenuItem openItem = new MenuItem("Open OmniDesk");
        openItem.addActionListener(e -> Platform.runLater(() -> {
            primaryStage.show();
            primaryStage.setIconified(false);
        }));

        MenuItem pauseItem = new MenuItem("Pause/Resume Tracker");
        pauseItem.addActionListener(e -> {
            // Toggle the tracker
            ActivityMonitorService.isRunning = !ActivityMonitorService.isRunning;
            System.out.println("Tracker Active: " + ActivityMonitorService.isRunning);
        });

        MenuItem exitItem = new MenuItem("Force Exit App");
        exitItem.addActionListener(e -> {
            Platform.exit();
            System.exit(0);
        });

        popup.add(openItem);
        popup.addSeparator();
        popup.add(pauseItem);
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println("TrayIcon could not be added.");
        }
    }
}