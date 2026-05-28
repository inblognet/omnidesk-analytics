package com.omnidesk.controllers;

import java.util.List;

import com.omnidesk.models.Task;
import com.omnidesk.services.TaskService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TasksController {

    @FXML private TextField taskInputField;
    @FXML private ListView<String> taskListView;

    private TaskService taskService = new TaskService();
    private ObservableList<String> displayItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Link the visual list to our internal data list
        taskListView.setItems(displayItems);
        // Load any existing tasks from the database when the screen opens
        refreshTaskList();
    }

    @FXML
    public void handleAddTask() {
        String title = taskInputField.getText().trim();

        if (!title.isEmpty()) {
            // 1. Create a new Task object
            Task newTask = new Task(title);

            // 2. Save it to the offline H2 Database
            taskService.saveTask(newTask);

            // 3. Clear the input box
            taskInputField.clear();

            // 4. Update the visual list
            refreshTaskList();
        }
    }

    private void refreshTaskList() {
        displayItems.clear();
        List<Task> tasksFromDb = taskService.getAllTasks();
        for (Task t : tasksFromDb) {
            displayItems.add("• " + t.getTitle());
        }
    }
}