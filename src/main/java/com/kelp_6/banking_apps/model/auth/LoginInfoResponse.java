package com.kelp_6.banking_apps.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfoResponse {
    private DetailLoginInfoResponse lastSuccessfullLoginAttempt;
    private DetailLoginInfoResponse failedLoginAttempt;
}
