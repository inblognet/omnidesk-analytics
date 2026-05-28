package com.omnidesk;

import java.util.prefs.Preferences;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.services.ActivityMonitorService;
import com.omnidesk.utils.SystemTrayManager;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    private static ActivityMonitorService tracker = new ActivityMonitorService();

    @Override
    public void init() throws Exception {
        System.out.println("Initializing OmniDesk Offline Database...");
        HibernateUtil.getSessionFactory();
        tracker.startTracking();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // PREVENT APP FROM DYING WHEN WINDOW CLOSES
        Platform.setImplicitExit(false);

        Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainDashboard.fxml"));
        Scene scene = new Scene(loader.load(), 1100, 700);

        primaryStage.setTitle("OmniDesk Analytics");
        primaryStage.setScene(scene);

        // Change 'X' button behavior to hide instead of close
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();
        });

        // Set up the System Tray Hook
        SystemTrayManager.setupTray(primaryStage, tracker);

        // Check if user wants to start minimized!
        Preferences prefs = Preferences.userNodeForPackage(AppLauncher.class);
        boolean startMinimized = prefs.getBoolean("startMinimized", false);

        if (!startMinimized) {
            primaryStage.show();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}