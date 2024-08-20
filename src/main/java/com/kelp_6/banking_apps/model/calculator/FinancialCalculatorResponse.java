package com.kelp_6.banking_apps.model.calculator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialCalculatorResponse {
    private Long needs;
    private Long wants;
    private Long savings;
    private Long invest;
    private Long debts;
}
