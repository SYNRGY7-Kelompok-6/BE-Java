package com.kelp_6.banking_apps.model.savedAccounts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSavedAccountRequest {
    @NotNull(message = "isFavorite field can't be empty")
    private Boolean isFavorite;

    @JsonIgnore
    private String savedBeneficiaryId;

    @JsonIgnore
    private String userID;
}
