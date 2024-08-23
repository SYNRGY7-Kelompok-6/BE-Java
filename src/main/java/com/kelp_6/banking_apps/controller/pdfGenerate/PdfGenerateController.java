package com.kelp_6.banking_apps.controller.pdfGenerate;

import com.kelp_6.banking_apps.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PdfGenerateController {
    private final static Logger LOGGER = LoggerFactory.getLogger(PdfGenerateController.class);
    private final PdfService pdfService;

    @GetMapping("/generate-pdf/{transactionId}")
    @ResponseBody
    public ResponseEntity<byte[]> generateInvoice(@PathVariable String transactionId) {
        LOGGER.info("accessed");

        byte[] pdfContent = pdfService.generatePdf(transactionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition
                .builder("attachment")
                .filename("transaction.pdf")
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);

    }
}
