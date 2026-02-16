package com.pizzaapp.PizzaWebApp.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.pizzaapp.PizzaWebApp.entity.Order;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfService {

    public void generateReceipt(Order order, HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // 1. Header Logic
        Font fontTitle = FontFactory.getFont(FontFactory.COURIER_BOLD, 18);
        Paragraph title = new Paragraph("PIZZA CORNER RECEIPT", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        document.add(new Paragraph("Order ID: " + order.getId(), FontFactory.getFont(FontFactory.COURIER, 10)));
        document.add(new Paragraph("Date: " + order.getOrderDate(), FontFactory.getFont(FontFactory.COURIER, 10)));
        document.add(new Paragraph("-----------------------------------------------------------"));

        // 2. Customer Details
        document.add(new Paragraph("Customer: " + order.getCustomerName()));
        document.add(new Paragraph("Address: " + order.getAddress()));
        document.add(new Paragraph("\n"));

        // 3. Items Table (Matching your HTML Columns)
        PdfPTable table = new PdfPTable(3); // Item, Qty, Price
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 1, 1}); // Give more space to the Item Name

        // Headers
        table.addCell("Item");
        table.addCell("Qty");
        table.addCell("Price");

        for (String itemStr : order.getItems()) {
            if (itemStr.contains("|")) {
                String[] details = itemStr.split("\\|"); // Escape the pipe for Regex
                table.addCell(details[0]); // Name
                table.addCell(details[1]); // Qty
                table.addCell("₹" + details[2]); // Price
            } else {
                table.addCell(itemStr);
                table.addCell("-");
                table.addCell("-");
            }
        }
        document.add(table);

        // 4. Totals (Matching your 5% Calculation)
        double total = order.getTotalAmount();
        double subtotal = total / 1.05;
        double serviceCharge = subtotal * 0.05;

        document.add(new Paragraph("\nSubtotal: ₹" + String.format("%.2手工", subtotal)));
        document.add(new Paragraph("Service Charge (5%): ₹" + String.format("%.2f", serviceCharge)));

        Font grandTotalFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 14);
        Paragraph grandTotal = new Paragraph("GRAND TOTAL: ₹" + String.format("%.2f", total), grandTotalFont);
        grandTotal.setAlignment(Element.ALIGN_RIGHT);
        document.add(grandTotal);

        document.close();
    }}