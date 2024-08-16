package com.kelp_6.banking_apps.controller.transfer.intrabank;

import com.kelp_6.banking_apps.controller.Testing;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferRequest;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferResponse;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.TransactionIntrabankService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfer-intrabank")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {
    private final static Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionIntrabankService transactionIntrabankService;

    @PostMapping(
            path = {"", "/"},
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebResponse<TransferResponse>> transfer(
            Authentication authentication,
            @RequestHeader("X-PIN-TOKEN") String pinToken,
            @RequestBody @Valid TransferRequest request) {
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        request.setUserID(userDetails.getUsername());
        request.setPinToken(pinToken);

        TransferResponse transferData = this.transactionIntrabankService.transfer(request);
        WebResponse<TransferResponse> response = WebResponse.<TransferResponse>builder()
                .status("success")
                .message("funds successfully send")
                .data(transferData)
                .build();
        return ResponseEntity.ok(response);
    }
}
