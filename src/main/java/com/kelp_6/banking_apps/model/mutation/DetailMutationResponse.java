package com.kelp_6.banking_apps.model.mutation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailMutationResponse {
    private String transactionId;
    private MutationBalanceResponse amount;
    private String transactionDate;
    private String remark;
    private String desc;
    private String type;
    private BeneficiaryAccountResponse beneficiaryAccount;
    private SourceAccountResponse sourceAccount;
}
