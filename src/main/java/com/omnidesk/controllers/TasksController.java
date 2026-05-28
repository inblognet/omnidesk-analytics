package com.omnidesk.controllers;

import java.util.List;

import org.hibernate.Session;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.Task;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TasksController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TableView<Task> tasksTable;

    private ObservableList<Task> tasksList = FXCollections.observableArrayList();
    private Task selectedTask = null;

    @FXML
    public void initialize() {
        // Setup dropdown options
        priorityComboBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
        statusComboBox.setItems(FXCollections.observableArrayList("Pending", "Completed"));

        // Set Defaults
        priorityComboBox.getSelectionModel().select("Medium");
        statusComboBox.getSelectionModel().select("Pending");

        tasksTable.setItems(tasksList);

        // Listen for clicks on the table
        tasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTask = newSelection;
                titleField.setText(selectedTask.getTitle());
                priorityComboBox.setValue(selectedTask.getPriority());
                statusComboBox.setValue(selectedTask.getStatus());
            }
        });

        loadTasks();
    }

    @FXML
    public void handleAddTask() {
        if (titleField.getText().isEmpty() || priorityComboBox.getValue() == null || statusComboBox.getValue() == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Task newTask = new Task(
                titleField.getText(),
                priorityComboBox.getValue(),
                statusComboBox.getValue()
            );
            session.persist(newTask);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error adding task.");
        }
        clearInputs();
        loadTasks();
    }

    @FXML
    public void handleUpdateTask() {
        if (selectedTask == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Apply the edits
            selectedTask.setTitle(titleField.getText());
            selectedTask.setPriority(priorityComboBox.getValue());
            selectedTask.setStatus(statusComboBox.getValue());

            session.merge(selectedTask); // Save changes to database
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating task.");
        }
        clearInputs();
        loadTasks();
    }

    @FXML
    public void handleDeleteTask() {
        if (selectedTask == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(selectedTask); // Delete from DB
            session.getTransaction().commit();
        }
        clearInputs();
        loadTasks();
    }

    private void clearInputs() {
        titleField.clear();
        priorityComboBox.getSelectionModel().select("Medium");
        statusComboBox.getSelectionModel().select("Pending");
        selectedTask = null;
        tasksTable.getSelectionModel().clearSelection();
    }

    private void loadTasks() {
        tasksList.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sort so pending tasks stay at the top, and completed drop to the bottom!
            List<Task> records = session.createQuery("from Task order by status desc, id desc", Task.class).list();
            tasksList.addAll(records);
        }
    }
}