package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.mutation.MutationRequest;
import com.kelp_6.banking_apps.model.mutation.MutationResponse;
import com.kelp_6.banking_apps.model.mutation.TransactionDetailRequest;
import com.kelp_6.banking_apps.model.mutation.TransactionDetailResponse;

public interface MutationService {
    MutationResponse getMutation(MutationRequest request);

    TransactionDetailResponse getDetailTransaction(TransactionDetailRequest request);
}
