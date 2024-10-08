package com.kelp_6.banking_apps.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PinValidationRequest {
    @NotNull(message = "pin must be filled properly")
    @Size(min = 6, max = 6, message = "pin's length must be 6")
    @Pattern(regexp = "\\d++", message = "Pin must contain numbers only")
    private String pin;

    @JsonIgnore
    private String userId;
}