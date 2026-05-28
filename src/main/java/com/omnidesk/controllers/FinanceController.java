package com.omnidesk.controllers;

import java.util.List;

import com.omnidesk.models.Transaction;
import com.omnidesk.services.FinanceService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class FinanceController {

    @FXML private Label balanceLabel;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField amountField;
    @FXML private TextField descriptionField;
    @FXML private ListView<String> transactionListView;

    private FinanceService financeService = new FinanceService();
    private ObservableList<String> displayItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        transactionListView.setItems(displayItems);
        // Default the dropdown to INCOME
        typeComboBox.getSelectionModel().selectFirst();
        refreshData();
    }

    @FXML
    public void handleAddTransaction() {
        String type = typeComboBox.getValue();
        String amountText = amountField.getText().trim();
        String description = descriptionField.getText().trim();

        if (amountText.isEmpty()) return;

        try {
            // Convert the text into a real decimal number
            double amount = Double.parseDouble(amountText);

            // 1. Create the Database Object
            Transaction newTransaction = new Transaction(type, amount, description);

            // 2. Save it offline
            financeService.saveTransaction(newTransaction);

            // 3. Clear the form
            amountField.clear();
            descriptionField.clear();

            // 4. Update the screen
            refreshData();

        } catch (NumberFormatException e) {
            System.err.println("Invalid amount entered! Please type a number.");
        }
    }

    private void refreshData() {
        // 1. Calculate and update the big Balance number
        double totalBalance = financeService.calculateTotalBalance();
        balanceLabel.setText(String.format("$%.2f", totalBalance));

        // Change color based on if you are in debt or not!
        if (totalBalance < 0) {
            balanceLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f85149;"); // Red
        } else {
            balanceLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #3fb950;"); // Green
        }

        // 2. Update the Ledger List
        displayItems.clear();
        List<Transaction> transactions = financeService.getAllTransactions();
        for (Transaction t : transactions) {
            String symbol = t.getType().equals("INCOME") ? "+" : "-";
            displayItems.add(String.format("[%s] %s$%.2f : %s", t.getType(), symbol, t.getAmount(), t.getDescription()));
        }
    }
}