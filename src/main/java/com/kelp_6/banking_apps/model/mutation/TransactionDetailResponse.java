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
    private String transaction_id;
    private Date date;
    private Double amount;
    private String remark;
    private String beneficiary_name;
    private String beneficiary_account;
    private String sender_name;
    private String sender_account;
}
