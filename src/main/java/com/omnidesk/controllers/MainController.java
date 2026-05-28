package com.omnidesk.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class MainController {

    @FXML
    private BorderPane mainContainer;

    @FXML
    public void initialize() {
        // Automatically load the Dashboard when the app starts!
        loadView("/views/DashboardView.fxml");
    }

    @FXML
    public void loadDashboardView(ActionEvent event) {
        loadView("/views/DashboardView.fxml");
    }

    @FXML
    public void loadTasksView(ActionEvent event) {
        loadView("/views/TasksView.fxml");
    }

    @FXML
    public void loadFinancesView(ActionEvent event) {
        loadView("/views/FinanceView.fxml");
    }

    @FXML
    public void loadInventoryView(ActionEvent event) {
        loadView("/views/InventoryView.fxml");
    }

    @FXML
    public void loadActivityView(javafx.event.ActionEvent event) {
        loadView("/views/ActivityView.fxml");
    }

    @FXML
    public void loadSettingsView(javafx.event.ActionEvent event) {
        loadView("/views/SettingsView.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}