package com.kelp_6.banking_apps.model.calculator;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinancialCalculatorRequest {
    @Min(value = 0, message = "available balance can't be negative")
    @NotNull(message = "available balance required")
    private Double availableBalance;

    @NotNull(message = "debt status required")
    private Boolean debt;
}
