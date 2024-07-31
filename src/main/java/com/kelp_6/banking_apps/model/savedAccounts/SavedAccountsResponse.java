package com.kelp_6.banking_apps.model.savedAccounts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedAccountsResponse {
    private String savedBeneficiaryId;
    private String beneficiaryAccountNumber;
    private String beneficiaryAccountName;
    private Boolean favorite;
}
