package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.transfer.intrabank.TransferRequest;
import com.kelp_6.banking_apps.model.transfer.intrabank.TransferResponse;

public interface TransactionIntrabankService {
    TransferResponse transfer(TransferRequest request);
}
