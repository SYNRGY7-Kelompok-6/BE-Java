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
public class AccountInfoResponse {
    private String accountNo;
    private String accountType;
    private Date accountCardExp;
    private String name;
    private String cvv;
    private AccountBalanceDetailsResponse accountBalance;
    private int pinExpiredTimeLeft;
}
