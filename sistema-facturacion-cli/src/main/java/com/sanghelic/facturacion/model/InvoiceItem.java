package com.sanghelic.facturacion.model;

public record InvoiceItem(
        Product product,
        int quantity
) {
}