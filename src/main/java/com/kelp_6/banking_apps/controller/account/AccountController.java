package com.kelp_6.banking_apps.controller.account;

import com.kelp_6.banking_apps.model.account.AvailableAccountRequest;
import com.kelp_6.banking_apps.model.account.AvailableAccountResponse;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    @GetMapping(
            value = {"/{accountNumber}", "/{accountNumber}/"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AvailableAccountResponse> findAccount(
            Authentication authentication,
            @PathVariable String accountNumber
    ){
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        AvailableAccountRequest request = new AvailableAccountRequest();
        request.setBeneficiaryAccountNumber(accountNumber);
        request.setUserID(userDetails.getUsername());

        AvailableAccountResponse availableAccountResponse = accountService.getAccount(request);

        return WebResponse.<AvailableAccountResponse>builder()
                .status("success")
                .message("account is available")
                .data(availableAccountResponse)
                .build();
    }
}
