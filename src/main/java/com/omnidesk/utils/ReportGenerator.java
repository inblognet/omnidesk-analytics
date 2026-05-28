package com.omnidesk.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.omnidesk.models.Item;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ReportGenerator {

    public static void exportInventoryToExcel(List<Item> items, File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Inventory Report");

            // Create Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Item Name", "Quantity", "Price ($)"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Fill Data Rows
            int rowNum = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getId());
                row.createCell(1).setCellValue(item.getName());
                row.createCell(2).setCellValue(item.getQuantity());
                row.createCell(3).setCellValue(item.getPrice());
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            System.out.println("Excel file saved successfully to: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Failed to generate Excel file.");
            e.printStackTrace();
        }
    }

    public static void exportInventoryToPDF(List<Item> items, File file) {
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add Title
            document.add(new Paragraph("OmniDesk: Official Inventory Report"));
            document.add(new Paragraph(" ")); // Blank space

            // Create a 4-column Table
            PdfPTable table = new PdfPTable(4);
            table.addCell("ID");
            table.addCell("Item Name");
            table.addCell("Quantity");
            table.addCell("Price ($)");

            // Fill Table Data
            for (Item item : items) {
                table.addCell(String.valueOf(item.getId()));
                table.addCell(item.getName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(String.valueOf(item.getPrice()));
            }

            document.add(table);
            System.out.println("PDF file saved successfully to: " + file.getAbsolutePath());

        } catch (Exception e) {
            System.err.println("Failed to generate PDF file.");
            e.printStackTrace();
        }
    }
}