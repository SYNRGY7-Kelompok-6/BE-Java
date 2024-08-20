package com.kelp_6.banking_apps.model.mutation;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleTransactionDetailResponse {

    private String sourceName;
    private Double amount;
    private String transactionDate;
}
