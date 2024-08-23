package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.Transaction;
import com.kelp_6.banking_apps.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfServiceImpl implements PdfService {
    private final static Logger LOGGER = LoggerFactory.getLogger(PdfServiceImpl.class);
    private final TemplateEngine templateEngine;
    private final TransactionRepository transactionRepository;

    @Override
    public byte[] generatePdf(String transactionId) {
        LOGGER.info("accessed");

        UUID id = UUID.fromString(transactionId);
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "transaction doesn't exist"));

        Context context = new Context();
        if (Objects.equals(transaction.getCurrency(), "IDR")) {
            context.setVariable("currencySymbol", "Rp");
        }
        context.setVariable("transactionDate", transaction.getTransactionDate());
        context.setVariable("transactionReference", transaction.getRefNumber());
        context.setVariable("recipientName", transaction.getBeneficiaryName());
        context.setVariable("accountNumber", transaction.getBeneficiaryAccountNumber());
        context.setVariable("transactionAmount", transaction.getAmount());
        context.setVariable("sourceName", transaction.getAccount().getUser().getName());
        context.setVariable("sourceAccountNumber", transaction.getAccount().getAccountNumber());
        context.setVariable("remark", transaction.getRemark());
        context.setVariable("description", transaction.getDescription());

        String htmlContent = templateEngine.process("transaction", context);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();

        try {
            renderer.createPDF(byteArrayOutputStream);
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }

        return byteArrayOutputStream.toByteArray();
    }
}
