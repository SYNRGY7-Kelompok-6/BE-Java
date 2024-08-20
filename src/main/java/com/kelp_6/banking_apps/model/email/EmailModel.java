package com.kelp_6.banking_apps.model.email;


import com.kelp_6.banking_apps.model.transfer.intrabank.Amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailModel {
    private String beneficiaryAccount;
    private String beneficiaryName;
    private String beneficiaryEmail;
    private Amount amount;
    private Date transactionDate;
    private String sender;
}
