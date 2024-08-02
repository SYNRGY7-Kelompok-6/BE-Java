package com.kelp_6.banking_apps.service;


import com.kelp_6.banking_apps.model.mutation.AccountInfoRequest;
import com.kelp_6.banking_apps.model.mutation.AccountInfoResponse;

public interface AccountInfoService {
    AccountInfoResponse getAccountInfo(AccountInfoRequest request);
}
