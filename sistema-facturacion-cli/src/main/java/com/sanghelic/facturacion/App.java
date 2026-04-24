package com.sanghelic.facturacion;

import com.sanghelic.facturacion.service.InvoiceService;
import com.sanghelic.facturacion.view.ConsoleView;

public class App {
    public static void main(String[] args) {
        InvoiceService invoiceService = new InvoiceService();
        ConsoleView consoleView = new ConsoleView(invoiceService);
        consoleView.start();
    }
}