package com.kelp_6.banking_apps.model.transfer.intrabank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "beneficiaryAccountNumber can't be blank")
    @Size(min = 10, max = 10, message = "account number's length must be 10")
    @Pattern(regexp = "\\d++", message = "account number should contain only numbers")
    private String beneficiaryAccountNumber;

    @NotBlank(message = "remark can't be blank")
    private String remark;

    private String desc;

    @Valid
    private Amount amount;
}
