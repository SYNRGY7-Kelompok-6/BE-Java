package com.kelp_6.banking_apps.model.transfer.intrabank;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Amount {
    @Positive
    double value;

    @NotBlank
    String currency;
}
