package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.auth.LoginRequest;
import com.kelp_6.banking_apps.model.auth.TokenResponse;

public interface AuthService {
    public TokenResponse login(LoginRequest request);
}
