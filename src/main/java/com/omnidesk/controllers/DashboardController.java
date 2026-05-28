package com.omnidesk.controllers;

import com.omnidesk.services.TaskService;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label totalTasksLabel;
    @FXML private BarChart<String, Number> activityChart;

    private TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        // 1. Update the Total Tasks Card from the Database
        int taskCount = taskService.getAllTasks().size();
        totalTasksLabel.setText(String.valueOf(taskCount));

        // 2. Populate the Chart with some data
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tasks Added");

        // Mock data for the week to make the chart look good
        series.getData().add(new XYChart.Data<>("Mon", 4));
        series.getData().add(new XYChart.Data<>("Tue", 7));
        series.getData().add(new XYChart.Data<>("Wed", 2));
        series.getData().add(new XYChart.Data<>("Thu", taskCount)); // Real data for today!
        series.getData().add(new XYChart.Data<>("Fri", 0));

        activityChart.getData().add(series);
    }
}