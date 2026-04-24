package com.sanghelic.facturacion.view;

import com.sanghelic.facturacion.model.Invoice;
import com.sanghelic.facturacion.model.InvoiceItem;
import com.sanghelic.facturacion.model.Product;
import com.sanghelic.facturacion.service.InvoiceService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleView {

    private final InvoiceService invoiceService;
    private final Scanner scanner;

    public ConsoleView(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        int option;

        do {
            printMenu();
            option = readInt("Select an option: ");

            switch (option) {
                case 1 -> createInvoice();
                case 2 -> showHistory();
                case 3 -> System.out.println("Exiting system...");
                default -> System.out.println("Invalid option.");
            }
        } while (option != 3);
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== Billing System CLI =====");
        System.out.println("1. New Invoice");
        System.out.println("2. View History");
        System.out.println("3. Exit");
    }

    private void createInvoice() {
        System.out.println();
        System.out.println("Available customers:");
        invoiceService.getCustomers().forEach(customer ->
                System.out.printf("%s - %s (%s)%n", customer.id(), customer.name(), customer.email())
        );

        System.out.print("Customer ID: ");
        String customerId = scanner.nextLine();

        List<InvoiceItem> items = new ArrayList<>();
        boolean addingItems = true;

        while (addingItems) {
            System.out.println();
            System.out.println("Available products:");
            invoiceService.getProducts().forEach(product ->
                    System.out.printf("%s - %s - $%.2f%n", product.sku(), product.name(), product.unitPrice())
            );

            System.out.print("Product SKU: ");
            String sku = scanner.nextLine();

            Product product = invoiceService.findProductBySku(sku)
                    .orElse(null);

            if (product == null) {
                System.out.println("Product not found.");
                continue;
            }

            int quantity = readInt("Quantity: ");
            items.add(new InvoiceItem(product, quantity));

            System.out.print("Add another product? (y/n): ");
            String answer = scanner.nextLine();
            addingItems = answer.equalsIgnoreCase("y");
        }

        if (items.isEmpty()) {
            System.out.println("Invoice cannot be created without items.");
            return;
        }

        try {
            Invoice invoice = invoiceService.generateInvoice(customerId, items);
            double subtotal = invoiceService.calculateSubtotal(invoice.items());
            double total = invoiceService.calculateTotal(invoice.items());

            System.out.println();
            System.out.println("Invoice generated successfully.");
            System.out.printf("Invoice ID: %s%n", invoice.id());
            System.out.printf("Customer: %s%n", invoice.customer().name());
            System.out.printf("Subtotal: $%.2f%n", subtotal);
            System.out.printf("Total with IVA: $%.2f%n", total);
            System.out.printf("Status: %s%n", invoice.status());
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void showHistory() {
        List<Invoice> history = invoiceService.getHistory();

        if (history.isEmpty()) {
            System.out.println("No invoices found.");
            return;
        }

        System.out.println();
        System.out.println("Invoice history:");

        history.forEach(invoice -> {
            double subtotal = invoiceService.calculateSubtotal(invoice.items());
            double total = invoiceService.calculateTotal(invoice.items());

            System.out.printf(
                    "%s | %s | %s | Subtotal: $%.2f | Total: $%.2f | Status: %s%n",
                    invoice.id(),
                    invoice.createdAt(),
                    invoice.customer().name(),
                    subtotal,
                    total,
                    invoice.status()
            );
        });

        System.out.print("Filter by date range? (y/n): ");
        String answer = scanner.nextLine();

        if (answer.equalsIgnoreCase("y")) {
            LocalDateTime start = LocalDateTime.now().minusDays(readInt("Days back from today: "));
            LocalDateTime end = LocalDateTime.now();

            List<Invoice> filtered = invoiceService.filterHistoryByDateRange(start, end);

            System.out.println();
            System.out.println("Filtered history:");
            filtered.forEach(invoice ->
                    System.out.printf("%s | %s | %s%n",
                            invoice.id(),
                            invoice.createdAt(),
                            invoice.customer().name())
            );
        }
    }

    private int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(scanner.nextLine());
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}