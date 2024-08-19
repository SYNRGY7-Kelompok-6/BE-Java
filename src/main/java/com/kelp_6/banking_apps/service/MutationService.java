package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.mutation.*;

import java.util.List;
public interface MutationService {
    MutationResponse getMutation(MutationRequest request);

    MutationsOnlyResponse getMutationsOnly(MutationRequest request);

    TransactionDetailResponse getDetailTransaction(TransactionDetailRequest request);

    AccountMonthlyResponse getMonthlyMutation(int month, String username);

    List<SimpleTransactionDetailResponse> getLastTwoCreditTransactions(LatestTransactionsRequest request);

}
