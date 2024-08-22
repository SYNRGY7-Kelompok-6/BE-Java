package com.kelp_6.banking_apps.controller.pdfGenerate;

import com.kelp_6.banking_apps.model.mutation.MutationsOnlyResponse;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.PdfService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class PdfGenerateController {
    private final PdfService pdfService;

    @GetMapping("/generate-pdf/{transactionId}")
    @ResponseBody
    public ResponseEntity<byte[]> generateInvoice(@PathVariable String transactionId) {

        byte[] pdfContent = pdfService.generatePdf(transactionId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename("transaction.pdf").build());
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);

    }
}
