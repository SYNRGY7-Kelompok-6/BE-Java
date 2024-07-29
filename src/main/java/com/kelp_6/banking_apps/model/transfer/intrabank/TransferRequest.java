package com.kelp_6.banking_apps.model.transfer.intrabank;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TransferRequest {
    @NotBlank
    @JsonIgnore
    private String userID;

    @NotBlank
    private String beneficiaryAccountNumber;

    private String remark;

    private Amount amount;
}
