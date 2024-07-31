package com.kelp_6.banking_apps.model.savedAccounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
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
