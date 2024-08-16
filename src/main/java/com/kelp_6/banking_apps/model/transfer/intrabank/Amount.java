package com.kelp_6.banking_apps.model.transfer.intrabank;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Amount {
    @NotNull(message = "amount can't be null")
    @DecimalMin(value = "100.0", inclusive = false, message = "amount must be greater than 100.0")
    private Double value;

    @NotBlank(message = "currency can't be blank")
    private String currency;
}
