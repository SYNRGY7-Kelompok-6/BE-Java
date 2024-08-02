package com.kelp_6.banking_apps.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableAccountResponse {
    private String beneficiaryAccountNumber;
    private String beneficiaryAccountName;
}
