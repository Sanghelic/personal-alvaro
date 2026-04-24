package com.sanghelic.facturacion.model;

import java.time.LocalDateTime;
import java.util.List;

public record Invoice(
        String id,
        Customer customer,
        List<InvoiceItem> items,
        InvoiceStatus status,
        LocalDateTime createdAt
) {
    public Invoice {
        items = List.copyOf(items);
    }
}