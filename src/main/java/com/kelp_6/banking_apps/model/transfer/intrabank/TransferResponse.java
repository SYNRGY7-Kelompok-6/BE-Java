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
    String refNumber;
    String transactionId;
    Amount amount;
    String transactionDate;
    String remark;
    String desc;
    String beneficiaryAccountNumber;
    String beneficiaryName;
    String sourceAccountNumber;
    String sourceName;
}
