package com.kelp_6.banking_apps.model.mutation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDetailResponse {
    private String transactionId;
    private String refNumber;
    private Double amount;
    private Date transactionDate;
    private String remark;
    private String desc;
    private String type;
    private String beneficiaryName;
    private String beneficiaryAccountNumber;
    private String sourceName;
    private String sourceAccountNumber;
}
