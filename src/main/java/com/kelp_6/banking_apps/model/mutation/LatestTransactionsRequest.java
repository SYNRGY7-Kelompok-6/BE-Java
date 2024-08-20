package com.kelp_6.banking_apps.model.mutation;

import lombok.Data;

@Data
public class LatestTransactionsRequest {
    private String userID;
    private int limit;
}
