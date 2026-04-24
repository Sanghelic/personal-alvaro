package com.sanghelic.facturacion.service;

import com.sanghelic.facturacion.model.Invoice;
import com.sanghelic.facturacion.model.InvoiceItem;
import com.sanghelic.facturacion.model.Product;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class InvoiceServiceTest {

    private final InvoiceService invoiceService = new InvoiceService();

    @Test
    void shouldCalculateSubtotalCorrectly() {
        Product product = invoiceService.findProductBySku("P001").orElseThrow();
        List<InvoiceItem> items = List.of(new InvoiceItem(product, 2));

        double subtotal = invoiceService.calculateSubtotal(items);

        assertEquals(9000.00, subtotal, 0.001);
    }

    @Test
    void shouldCalculateTotalWithIvaCorrectly() {
        Product product = invoiceService.findProductBySku("P001").orElseThrow();
        List<InvoiceItem> items = List.of(new InvoiceItem(product, 2));

        double total = invoiceService.calculateTotal(items);

        assertEquals(10440.00, total, 0.001);
    }

    @Test
    void shouldFilterHistoryByDateRangeCorrectly() {
        Product product = invoiceService.findProductBySku("P001").orElseThrow();
        List<InvoiceItem> items = List.of(new InvoiceItem(product, 1));

        Invoice invoice = invoiceService.generateInvoice("C001", items);

        LocalDateTime start = invoice.createdAt().minusMinutes(1);
        LocalDateTime end = invoice.createdAt().plusMinutes(1);

        List<Invoice> filtered = invoiceService.filterHistoryByDateRange(start, end);

        assertFalse(filtered.isEmpty());
        assertEquals(invoice.id(), filtered.getFirst().id());
    }

    @Test
    void shouldExportInvoicesUsingPecsConsumer() {
        Product product = invoiceService.findProductBySku("P002").orElseThrow();
        List<InvoiceItem> items = List.of(new InvoiceItem(product, 1));

        invoiceService.generateInvoice("C002", items);

        List<Object> exportedInvoices = new ArrayList<>();
        invoiceService.exportInvoices(exportedInvoices);

        assertFalse(exportedInvoices.isEmpty());
    }
}