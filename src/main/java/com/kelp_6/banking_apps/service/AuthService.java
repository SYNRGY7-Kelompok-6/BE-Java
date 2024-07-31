package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.auth.LoginInfoResponse;
import com.kelp_6.banking_apps.model.auth.LoginRequest;
import com.kelp_6.banking_apps.model.auth.TokenResponse;
import com.kelp_6.banking_apps.model.auth.*;

public interface AuthService {
    public TokenResponse login(LoginRequest request);
    public LoginInfoResponse getLoginInfo(String userID);
    public PinTokenResponse validatePin(PinValidationRequest pinValidationRequest);
}
