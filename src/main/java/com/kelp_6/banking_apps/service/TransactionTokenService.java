package com.kelp_6.banking_apps.service;

import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

public interface TransactionTokenService {
    public String generateTransactionToken(String accountNumber);
    public boolean validateTransactionToken(String token, String accountNumber);
    public String extractAccountNumber(String token);
    public <T> T extractTransactionClaim(String token, Function<Claims, T> claimsResolver);
    public Claims extractTransactionClaims(String token);
    public Date extractTransactionTokenExpiration(String token);
    public boolean isTransactionTokenExpired(String token);
}
