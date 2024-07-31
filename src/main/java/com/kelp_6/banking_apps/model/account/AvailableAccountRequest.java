package com.kelp_6.banking_apps.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvailableAccountRequest {
    @NotBlank(message = "beneficiary account number can't be blank")
    @Size(min = 10, max = 10, message = "beneficiary account number's length should be equal with 10")
    @Pattern(regexp = "\\d++", message = "account number must contain numbers only")
    @JsonIgnore
    private String beneficiaryAccountNumber;

    @JsonIgnore
    private String userID;
}
