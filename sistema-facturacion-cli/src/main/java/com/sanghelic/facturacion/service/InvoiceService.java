package com.sanghelic.facturacion.service;

import com.sanghelic.facturacion.annotation.Auditable;
import com.sanghelic.facturacion.functional.TaxStrategy;
import com.sanghelic.facturacion.model.Customer;
import com.sanghelic.facturacion.model.Invoice;
import com.sanghelic.facturacion.model.InvoiceItem;
import com.sanghelic.facturacion.model.InvoiceStatus;
import com.sanghelic.facturacion.model.Product;
import com.sanghelic.facturacion.model.ProductCategory;
import com.sanghelic.facturacion.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InvoiceService {

    private final Repository<Invoice> invoiceRepository = new Repository<>();

    private final List<Customer> customers = List.of(
            new Customer("C001", "Edelgard von Hresvelg", "emperor@adrestia.com"),
            new Customer("C002", "Dimitri Alexandre Blaiddyd", "king@faerghus.com"),
            new Customer("C003", "Claude von Riegan", "leader@leicester.com"),
            new Customer("C004", "Byleth Eisner", "professor@garreg.com"),
            new Customer("C005", "Rhea Seiros", "archbishop@central.com")
    );

    private final List<Product> products = List.of(
            new Product("P001", "Cloud Consulting Package", ProductCategory.CONSULTING, 4500.00),
            new Product("P002", "Software License", ProductCategory.SOFTWARE, 1200.00),
            new Product("P003", "Server Configuration", ProductCategory.SERVICE, 2500.00),
            new Product("P004", "Monthly Support Plan", ProductCategory.SUBSCRIPTION, 800.00),
            new Product("P005", "Network Router", ProductCategory.HARDWARE, 1800.00),
            new Product("P006", "Security Audit", ProductCategory.SERVICE, 3200.00),
            new Product("P007", "Backup Solution", ProductCategory.SOFTWARE, 1500.00),
            new Product("P008", "Workstation Setup", ProductCategory.HARDWARE, 2200.00)
    );

    private final TaxStrategy ivaStrategy = subtotal -> subtotal * 0.16;

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Optional<Customer> findCustomerById(String id) {
        return customers.stream()
                .filter(customer -> customer.id().equalsIgnoreCase(id))
                .findFirst();
    }

    public Optional<Product> findProductBySku(String sku) {
        return products.stream()
                .filter(product -> product.sku().equalsIgnoreCase(sku))
                .findFirst();
    }

    public double calculateSubtotal(List<InvoiceItem> items) {
        return items.stream()
                .mapToDouble(item -> item.quantity() * item.product().unitPrice())
                .sum();
    }

    public double calculateTotal(List<InvoiceItem> items) {
        double subtotal = calculateSubtotal(items);
        return subtotal + ivaStrategy.calculateTax(subtotal);
    }

    @Auditable(action = "GENERATE_INVOICE")
    public Invoice generateInvoice(String customerId, List<InvoiceItem> items) {
        Customer customer = findCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        Invoice invoice = new Invoice(
                UUID.randomUUID().toString(),
                customer,
                items,
                InvoiceStatus.PENDING,
                LocalDateTime.now()
        );

        invoiceRepository.save(invoice);
        return invoice;
    }

    public List<Invoice> getHistory() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> filterHistoryByDateRange(LocalDateTime start, LocalDateTime end) {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> !invoice.createdAt().isBefore(start) && !invoice.createdAt().isAfter(end))
                .toList();
    }

    public void importInvoices(List<? extends Invoice> invoices) {
        invoiceRepository.saveAll(invoices);
    }

    public void exportInvoices(List<? super Invoice> destination) {
        invoiceRepository.exportTo(destination);
    }
}