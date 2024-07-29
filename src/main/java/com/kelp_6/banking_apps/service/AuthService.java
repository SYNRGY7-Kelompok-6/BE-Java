package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.auth.*;

public interface AuthService {
    public TokenResponse login(LoginRequest request);
    public LoginInfoResponse getLoginInfo(String userID);
    public PinTokenResponse validatePin(PinValidationRequest pinValidationRequest);
}
