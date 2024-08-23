package com.kelp_6.banking_apps.controller;

import com.kelp_6.banking_apps.model.email.EmailModel;
import com.kelp_6.banking_apps.model.transfer.intrabank.Amount;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class Testing {
    private final static Logger LOGGER = LoggerFactory.getLogger(Testing.class);

    @Autowired
    private EmailService emailService;

    @GetMapping(
            path = "/ping",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<WebResponse<String>> tes(){
        LOGGER.info("accessed");

        WebResponse<String> response = WebResponse.<String>builder()
                .status("success")
                .message("server is running")
                .data("testing OK!")
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping(
            path = "/email-testing"
    )
    public void tesEmail() throws Exception{
        LOGGER.info("accessed");
        Amount amount = new Amount();
        amount.setCurrency("IDR");
        amount.setValue(12000.0);

        EmailModel data = new EmailModel();
        data.setSender("kelompok 6");
        data.setBeneficiaryEmail("inikel6synrgy@gmail.com");
        data.setBeneficiaryAccount("testing");
        data.setAmount(amount);
        data.setTransactionDate(new Date());
        data.setBeneficiaryName("testing");

        emailService.notificationIncomingFunds(data);
    }
}
