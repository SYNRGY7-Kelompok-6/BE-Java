package com.kelp_6.banking_apps.model.mutation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountBalanceDetailsResponse {
    private BalanceResponse availableBalance;
    private BalanceResponse holdAmount;
}
