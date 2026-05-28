package com.omnidesk.controllers;

import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML private VBox sidebar;
    @FXML private StackPane contentArea;
    @FXML private Label logoLabel;

    @FXML private Button btnHome, btnFinance, btnInventory, btnActivity, btnTasks, btnTheme, btnSettings;

    private boolean isSidebarExpanded = true;
    private boolean isDarkMode = true;

    @FXML
    public void initialize() {
        // Now that the Dashboard is built, we load it as the default home screen!
        loadOverview();
    }

    // --- VIEW ROUTING ---
    @FXML
    public void loadOverview() {
        loadView("/views/DashboardView.fxml");
    }

    @FXML
    public void loadFinancesView() { loadView("/views/FinancesView.fxml"); }

    @FXML
    public void loadInventoryView() { loadView("/views/InventoryView.fxml"); }

    @FXML
    public void loadActivityView() { loadView("/views/ActivityView.fxml"); }

    @FXML
    public void loadTasksView() { loadView("/views/TasksView.fxml"); }

    @FXML
    public void loadSettingsView() { loadView("/views/SettingsView.fxml"); }

    // Helper method to swap the center screen
    private void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to load view: " + fxmlPath);
        }
    }

    // --- SIDEBAR SLIDER LOGIC ---
    @FXML
    public void toggleSidebar() {
        isSidebarExpanded = !isSidebarExpanded;

        if (isSidebarExpanded) {
            sidebar.setPrefWidth(220);
            logoLabel.setVisible(true);
            logoLabel.setManaged(true);
            setButtonText(btnHome, "🏠 Dashboard");
            setButtonText(btnFinance, "💰 Finances");
            setButtonText(btnInventory, "📦 Inventory");
            setButtonText(btnActivity, "⏱ Activity");
            setButtonText(btnTasks, "✅ Tasks");
            setButtonText(btnTheme, isDarkMode ? "☀️ Light Mode" : "🌙 Dark Mode");
            setButtonText(btnSettings, "⚙ Settings");
        } else {
            sidebar.setPrefWidth(60);
            logoLabel.setVisible(false);
            logoLabel.setManaged(false);
            setButtonText(btnHome, "🏠");
            setButtonText(btnFinance, "💰");
            setButtonText(btnInventory, "📦");
            setButtonText(btnActivity, "⏱");
            setButtonText(btnTasks, "✅");
            setButtonText(btnTheme, isDarkMode ? "☀️" : "🌙");
            setButtonText(btnSettings, "⚙");
        }
    }

    private void setButtonText(Button btn, String text) {
        btn.setText(text);
    }

    // --- THEME ENGINE LOGIC ---
    @FXML
    public void toggleTheme() {
        isDarkMode = !isDarkMode;

        if (isDarkMode) {
            Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());
            if (isSidebarExpanded) btnTheme.setText("☀️ Light Mode");
            else btnTheme.setText("☀️");
        } else {
            Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
            if (isSidebarExpanded) btnTheme.setText("🌙 Dark Mode");
            else btnTheme.setText("🌙");
        }
    }
}