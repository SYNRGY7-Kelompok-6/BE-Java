package com.kelp_6.banking_apps.model.savedAccounts;

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
public class SavedAccountsRequest {
    @NotBlank(message = "account number can't be blank")
    @Size(min = 10, max = 10, message = "account number's length must be 10")
    @Pattern(regexp = "\\d++", message = "account number should contain only numbers")
    private String beneficiaryAccountNumber;

    @JsonIgnore
    private String savedBeneficiaryId;

    @JsonIgnore
    private String beneficiaryName;

    @JsonIgnore
    private Boolean isFavorite;

    @JsonIgnore
    private String userID;
}
