package com.omnidesk.controllers;

import java.io.File;
import java.util.List;

import org.hibernate.Session; // Added for exports
import org.hibernate.Transaction;

import com.omnidesk.config.HibernateUtil;
import com.omnidesk.models.Item;
import com.omnidesk.utils.ReportGenerator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList; // Added for the save dialog
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField; // Added for file handling
import javafx.stage.FileChooser;

public class InventoryController {

    @FXML private TextField nameField;
    @FXML private TextField qtyField;
    @FXML private TextField priceField;
    @FXML private TableView<Item> inventoryTable;

    private ObservableList<Item> inventoryList = FXCollections.observableArrayList();
    private Item selectedItem = null; // Remembers what row you clicked on!

    @FXML
    public void initialize() {
        inventoryTable.setItems(inventoryList);

        // Listen for clicks on the table
        inventoryTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedItem = newSelection;
                nameField.setText(selectedItem.getName());
                qtyField.setText(String.valueOf(selectedItem.getQuantity()));
                priceField.setText(String.valueOf(selectedItem.getPrice()));
            }
        });

        loadInventory();
    }

    @FXML
    public void handleAddItem() {
        if (nameField.getText().isEmpty() || qtyField.getText().isEmpty() || priceField.getText().isEmpty()) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            Item newItem = new Item(
                nameField.getText(),
                Integer.parseInt(qtyField.getText()),
                Double.parseDouble(priceField.getText())
            );
            session.persist(newItem);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error adding item. Check your numbers.");
        }
        clearInputs();
        loadInventory();
    }

    @FXML
    public void handleUpdateItem() {
        if (selectedItem == null) return; // Must click an item first

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Update the object
            selectedItem.setName(nameField.getText());
            selectedItem.setQuantity(Integer.parseInt(qtyField.getText()));
            selectedItem.setPrice(Double.parseDouble(priceField.getText()));

            // Merge saves the changes back to the database
            session.merge(selectedItem);
            tx.commit();
        } catch (Exception e) {
            System.err.println("Error updating item.");
        }
        clearInputs();
        loadInventory();
    }

    @FXML
    public void handleDeleteItem() {
        if (selectedItem == null) return;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.remove(selectedItem); // Delete from DB
            tx.commit();
        }
        clearInputs();
        loadInventory();
    }

    // --- NEW EXPORT METHODS ---

    @FXML
    public void handleExportPDF() {
        if (inventoryList.isEmpty()) return; // Don't export an empty table!

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Document", "*.pdf"));
        fileChooser.setInitialFileName("Inventory_Report.pdf");

        File file = fileChooser.showSaveDialog(inventoryTable.getScene().getWindow());

        if (file != null) {
            ReportGenerator.exportInventoryToPDF(inventoryList, file);
        }
    }

    @FXML
    public void handleExportExcel() {
        if (inventoryList.isEmpty()) return; // Don't export an empty table!

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Excel Report");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));
        fileChooser.setInitialFileName("Inventory_Report.xlsx");

        File file = fileChooser.showSaveDialog(inventoryTable.getScene().getWindow());

        if (file != null) {
            ReportGenerator.exportInventoryToExcel(inventoryList, file);
        }
    }

    // --------------------------

    private void clearInputs() {
        nameField.clear();
        qtyField.clear();
        priceField.clear();
        selectedItem = null;
        inventoryTable.getSelectionModel().clearSelection();
    }

    private void loadInventory() {
        inventoryList.clear();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Item> items = session.createQuery("from Item", Item.class).list();
            inventoryList.addAll(items);
        }
    }
}