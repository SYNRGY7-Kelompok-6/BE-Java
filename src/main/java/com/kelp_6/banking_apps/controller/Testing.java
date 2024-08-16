package com.kelp_6.banking_apps.controller;


import com.kelp_6.banking_apps.model.web.WebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testing {
    private final static Logger LOGGER = LoggerFactory.getLogger(Testing.class);

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
}
