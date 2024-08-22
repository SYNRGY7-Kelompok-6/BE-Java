package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.Account;
import com.kelp_6.banking_apps.model.account.AvailableAccountRequest;
import com.kelp_6.banking_apps.model.account.AvailableAccountResponse;
import com.kelp_6.banking_apps.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService{
    private final static Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final ValidationService validationService;

    @Override
    public AvailableAccountResponse getAccount(AvailableAccountRequest request) {
        LOGGER.info("accessed");

        validationService.validate(request);

        Account account = accountRepository.findByAccountNumber(request.getBeneficiaryAccountNumber()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account number doesn't exists"));

        if(account.getUser().getUserID().equalsIgnoreCase(request.getUserID())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "own account number");
        }

        return AvailableAccountResponse.builder()
                .beneficiaryAccountName(account.getUser().getName())
                .beneficiaryAccountNumber(account.getAccountNumber())
                .build();
    }
}
