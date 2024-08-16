package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.account.AvailableAccountRequest;
import com.kelp_6.banking_apps.model.account.AvailableAccountResponse;

public interface AccountService {
    AvailableAccountResponse getAccount(AvailableAccountRequest request);
}
