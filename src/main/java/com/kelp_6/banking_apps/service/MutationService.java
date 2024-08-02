package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.mutation.*;

public interface MutationService {

    TransactionDetailResponse getDetailTransaction(TransactionDetailRequest request);

    AccountMonthlyResponse getMonthlyMutation(int month, String username);
}
