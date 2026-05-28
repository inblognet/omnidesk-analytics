package com.omnidesk.controllers;

import java.util.Map;

import org.hibernate.Session;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.services.ActivityMonitorService;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label lblTasks;
    @FXML private Label lblFinance;
    @FXML private Label lblApp;
    @FXML private Label lblInventory;

    @FXML
    public void initialize() {
        loadMetrics();
    }

    private void loadMetrics() {
        // --- 1. Top OS App (Pulled from live memory) ---
        String topApp = "None";
        int maxSeconds = 0;

        for (Map.Entry<String, Integer> entry : ActivityMonitorService.todaysUsage.entrySet()) {
            if (entry.getValue() > maxSeconds) {
                maxSeconds = entry.getValue();
                topApp = entry.getKey();
            }
        }

        if (!topApp.equals("None")) {
            lblApp.setText(topApp + " (" + (maxSeconds / 60) + "m)");
        }

        // --- 2. Database Metrics ---
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Count all Tasks
            Long taskCount = session.createQuery("select count(t) from Task t", Long.class).uniqueResult();
            lblTasks.setText(taskCount != null ? String.valueOf(taskCount) : "0");

            // Count all Inventory Items
            Long itemCount = session.createQuery("select count(i) from Item i", Long.class).uniqueResult();
            lblInventory.setText(itemCount != null ? String.valueOf(itemCount) : "0");

            // Count all Financial Transactions
            Long transCount = session.createQuery("select count(t) from Transaction t", Long.class).uniqueResult();
            lblFinance.setText(transCount != null ? String.valueOf(transCount) : "0");

        } catch (Exception e) {
            System.err.println("Could not load database metrics.");
        }
    }
}