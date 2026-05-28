package com.omnidesk.controllers;

import java.util.List;

import com.omnidesk.models.Item;
import com.omnidesk.services.InventoryService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class InventoryController {

    @FXML private TextField nameField;
    @FXML private TextField qtyField;
    @FXML private TextField priceField;
    @FXML private ListView<String> inventoryListView;

    private InventoryService inventoryService = new InventoryService();
    private ObservableList<String> displayItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        inventoryListView.setItems(displayItems);
        refreshData();
    }

    @FXML
    public void handleAddItem() {
        String name = nameField.getText().trim();
        String qtyText = qtyField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || qtyText.isEmpty() || priceText.isEmpty()) return;

        try {
            int quantity = Integer.parseInt(qtyText);
            double price = Double.parseDouble(priceText);

            Item newItem = new Item(name, quantity, price);
            inventoryService.saveItem(newItem);

            nameField.clear();
            qtyField.clear();
            priceField.clear();

            refreshData();

        } catch (NumberFormatException e) {
            System.err.println("Invalid numbers entered for Quantity or Price.");
        }
    }

    private void refreshData() {
        displayItems.clear();
        List<Item> items = inventoryService.getAllItems();

        // Create a nice header for our list
        displayItems.add(String.format("%-30s | %-10s | %s", "PRODUCT NAME", "QUANTITY", "PRICE"));
        displayItems.add("---------------------------------------------------------------");

        for (Item i : items) {
            displayItems.add(String.format("%-30s | %-10d | $%.2f", i.getName(), i.getQuantity(), i.getPrice()));
        }
    }
}