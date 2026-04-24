package com.sanghelic.facturacion.functional;

@FunctionalInterface
public interface TaxStrategy {
    double calculateTax(double subtotal);
}