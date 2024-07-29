package com.kelp_6.banking_apps.model.transfer.intrabank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {
    String transactionId;
    Amount amount;
    Date transactionDate;
    String beneficiaryAccountNumber;
    String beneficiaryName;
    String sourceAccountNumber;
    String sourceName;
}
