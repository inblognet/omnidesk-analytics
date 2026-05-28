package com.omnidesk.controllers;

import java.io.File;
import java.util.prefs.Preferences;

import com.omnidesk.AppLauncher;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class SettingsController {

    @FXML private CheckBox startupToggle;
    @FXML private CheckBox minimizeToggle;
    @FXML private Label statusLabel;

    private Preferences prefs = Preferences.userNodeForPackage(AppLauncher.class);

    @FXML
    public void initialize() {
        startupToggle.setSelected(prefs.getBoolean("runOnStartup", false));
        minimizeToggle.setSelected(prefs.getBoolean("startMinimized", false));
    }

    @FXML
    public void handleSaveSettings() {
        boolean runOnStartup = startupToggle.isSelected();
        boolean startMinimized = minimizeToggle.isSelected();

        prefs.putBoolean("runOnStartup", runOnStartup);
        prefs.putBoolean("startMinimized", startMinimized);

        updateWindowsStartup(runOnStartup);

        statusLabel.setText("Preferences saved successfully!");
    }

    private void updateWindowsStartup(boolean enable) {
        try {
            // FIXED: Using AppLauncher.class instead of Main.class
            String jarPath = new File(AppLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();

            String runCommand = "javaw -jar \"" + jarPath + "\"";

            ProcessBuilder pb;
            if (enable) {
                pb = new ProcessBuilder("reg", "add", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run", "/v", "OmniDesk", "/t", "REG_SZ", "/d", runCommand, "/f");
            } else {
                pb = new ProcessBuilder("reg", "delete", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run", "/v", "OmniDesk", "/f");
            }

            pb.start();

        } catch (Exception e) {
            System.err.println("Could not modify Windows Registry.");
        }
    }
}