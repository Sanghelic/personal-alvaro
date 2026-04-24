package com.sanghelic.facturacion.model;

public record Product(
        String sku,
        String name,
        ProductCategory category,
        double unitPrice
) {
}