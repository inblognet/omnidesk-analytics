package com.omnidesk.controllers;

import java.util.List;

import org.hibernate.Session;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FinancesController {

    // Your awesome Balance Label is back!
    @FXML private Label balanceLabel;

    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField descriptionField; // Updated to match your model!
    @FXML private TextField amountField;
    @FXML private TableView<Transaction> financeTable;

    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private Transaction selectedTransaction = null;

    @FXML
    public void initialize() {
        typeComboBox.setItems(FXCollections.observableArrayList("INCOME", "EXPENSE"));
        typeComboBox.getSelectionModel().selectFirst();

        financeTable.setItems(transactionList);

        // Listen for clicks on the table
        financeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTransaction = newSelection;
                typeComboBox.setValue(selectedTransaction.getType());
                descriptionField.setText(selectedTransaction.getDescription());
                amountField.setText(String.valueOf(selectedTransaction.getAmount()));
            }
        });

        refreshData();
    }

    @FXML
    public void handleAddTransaction() {
        if (descriptionField.getText().isEmpty() || amountField.getText().isEmpty() || typeComboBox.getValue() == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Transaction newTrans = new Transaction(
                typeComboBox.getValue(),
                Double.parseDouble(amountField.getText()),
                descriptionField.getText()
            );
            session.persist(newTrans);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error adding transaction. Make sure amount is a number.");
        }
        clearInputs();
        refreshData();
    }

    @FXML
    public void handleUpdateTransaction() {
        if (selectedTransaction == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            // Apply the edits
            selectedTransaction.setType(typeComboBox.getValue());
            selectedTransaction.setDescription(descriptionField.getText());
            selectedTransaction.setAmount(Double.parseDouble(amountField.getText()));

            session.merge(selectedTransaction); // Save changes to database
            session.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error updating transaction.");
        }
        clearInputs();
        refreshData();
    }

    @FXML
    public void handleDeleteTransaction() {
        if (selectedTransaction == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(selectedTransaction); // Delete from DB
            session.getTransaction().commit();
        }
        clearInputs();
        refreshData();
    }

    private void clearInputs() {
        typeComboBox.getSelectionModel().selectFirst();
        descriptionField.clear();
        amountField.clear();
        selectedTransaction = null;
        financeTable.getSelectionModel().clearSelection();
    }

    private void refreshData() {
        transactionList.clear();
        double totalBalance = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Transaction> records = session.createQuery("from Transaction order by createdAt desc", Transaction.class).list();
            transactionList.addAll(records);

            // Calculate Balance dynamically
            for (Transaction t : records) {
                if ("INCOME".equals(t.getType())) {
                    totalBalance += t.getAmount();
                } else {
                    totalBalance -= t.getAmount();
                }
            }
        }

        // Apply your dynamic color styling!
        balanceLabel.setText(String.format("$%.2f", totalBalance));
        if (totalBalance < 0) {
            balanceLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f85149;"); // Red
        } else {
            balanceLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #3fb950;"); // Green
        }
    }
}