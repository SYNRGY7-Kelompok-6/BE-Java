package com.kelp_6.banking_apps.model.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PinValidationRequest {
    @NotNull(message = "pin must be filled properly")
    @Size(min = 6, max = 6, message = "pin length must be 6")
    private String pin;
}