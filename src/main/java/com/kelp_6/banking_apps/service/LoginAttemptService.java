package com.kelp_6.banking_apps.service;

public interface LoginAttemptService {
    void logFailedAttempt(String userID, String ipAddress);
}
