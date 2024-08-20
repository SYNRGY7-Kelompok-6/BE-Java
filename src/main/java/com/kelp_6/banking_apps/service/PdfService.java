package com.kelp_6.banking_apps.service;

public interface PdfService {
    byte[] generatePdf(String transactionId);
}
