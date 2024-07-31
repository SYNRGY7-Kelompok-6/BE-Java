package com.kelp_6.banking_apps.model.transfer.intrabank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {
    @JsonIgnore
    private String userID;

    @JsonIgnore
    private String pinToken;

    @NotBlank
    private String beneficiaryAccountNumber;

    private String remark;

    private String description;

    private Amount amount;
}
