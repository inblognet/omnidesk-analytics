package com.omnidesk.controllers;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.AppLimit;
import com.omnidesk.models.SearchLog;
import com.omnidesk.services.ActivityMonitorService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class ActivityController {

    @FXML private TextField appNameField;
    @FXML private TextField limitField;
    @FXML private ListView<String> limitsListView;
    @FXML private PieChart activityPieChart;
    @FXML private ListView<String> searchesListView;

    private ObservableList<String> displayLimits = FXCollections.observableArrayList();
    private ObservableList<String> displaySearches = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        limitsListView.setItems(displayLimits);
        searchesListView.setItems(displaySearches);

        // NEW: Selection listener. Clicking a list item auto-fills the inputs
        limitsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    // Parse out the app name and minutes from the string format "⏳ AppName - XX mins max"
                    String clean = newVal.replace("⏳ ", "");
                    String appName = clean.split(" - ")[0].trim();
                    String mins = clean.split(" - ")[1].replace(" mins max", "").trim();

                    appNameField.setText(appName);
                    limitField.setText(mins);
                } catch (Exception e) {
                    // Fail-safe in case of parsing issues
                }
            }
        });

        refreshLimitsList();
        refreshChart();
        refreshSearches();
    }

    @FXML
    public void handleSaveLimit() {
        String appName = appNameField.getText().trim();
        String limitText = limitField.getText().trim();

        if (appName.isEmpty() || limitText.isEmpty()) return;

        try {
            int minutes = Integer.parseInt(limitText);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();

                // Check if this app configuration already exists in the database
                AppLimit existingLimit = session.createQuery("from AppLimit where appName = :name", AppLimit.class)
                                                .setParameter("name", appName)
                                                .uniqueResult();

                if (existingLimit != null) {
                    // UPDATE: If it exists, overwrite the minutes restriction
                    existingLimit.setTimeSpentToday(0); // Optional: Reset calculation window on edit
                    session.merge(new AppLimit(appName, minutes));
                    // Using query fallback execution to force immediate update if tracking state is cached
                    session.createMutationQuery("update AppLimit set timeLimitMinutes = :mins where appName = :name")
                           .setParameter("mins", minutes)
                           .setParameter("name", appName)
                           .executeUpdate();
                } else {
                    // CREATE: If it doesn't exist, insert a fresh record
                    AppLimit newLimit = new AppLimit(appName, minutes);
                    session.persist(newLimit);
                }

                tx.commit();
            }

            clearInputsAndRefresh();

        } catch (Exception e) {
            System.err.println("Error saving/updating limit: Check input formats.");
            e.printStackTrace();
        }
    }

    // NEW: Deletes the specified application restriction from the database
    @FXML
    public void handleDeleteLimit() {
        String appName = appNameField.getText().trim();

        if (appName.isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Wipe the record using a clean mutation query
            session.createMutationQuery("delete from AppLimit where appName = :name")
                   .setParameter("name", appName)
                   .executeUpdate();

            tx.commit();
            clearInputsAndRefresh();
        } catch (Exception e) {
            System.err.println("Error deleting limit.");
        }
    }

    private void clearInputsAndRefresh() {
        appNameField.clear();
        limitField.clear();
        limitsListView.getSelectionModel().clearSelection();
        refreshLimitsList();
    }

    private void refreshLimitsList() {
        displayLimits.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<AppLimit> limits = session.createQuery("from AppLimit", AppLimit.class).list();
            for (AppLimit limit : limits) {
                displayLimits.add("⏳ " + limit.getAppName() + " - " + limit.getTimeLimitMinutes() + " mins max");
            }
        }
    }

    @FXML
    public void refreshChart() {
        activityPieChart.getData().clear();
        Map<String, Integer> usageData = ActivityMonitorService.todaysUsage;

        for (Map.Entry<String, Integer> entry : usageData.entrySet()) {
            double minutesUsed = entry.getValue() / 60.0;
            if (minutesUsed > 0.1) {
                PieChart.Data slice = new PieChart.Data(entry.getKey() + String.format(" (%.1fm)", minutesUsed), minutesUsed);
                activityPieChart.getData().add(slice);
            }
        }
    }

    @FXML
    public void refreshSearches() {
        displaySearches.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<SearchLog> recentSearches = session.createQuery("from SearchLog order by searchTime desc", SearchLog.class)
                                                    .setMaxResults(15)
                                                    .list();
            for (SearchLog log : recentSearches) {
                displaySearches.add(log.getFormattedTime() + " | [" + log.getEngine() + "] " + log.getQuery());
            }
        } catch (Exception e) {
            System.err.println("Could not load searches.");
        }
    }
}