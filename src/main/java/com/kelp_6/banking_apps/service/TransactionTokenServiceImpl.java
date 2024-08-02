package com.kelp_6.banking_apps.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class TransactionTokenServiceImpl implements TransactionTokenService {
    @Value("${security.jwt.transaction-secret-key}")
    private String TRANSACTION_SECRET_KEY;

    @Value("${security.jwt.transaction-expired-milliseconds}")
    private long TRANSACTION_EXPIRATION_TIME;
    private final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

    public String generateTransactionToken(String accountNumber) {
        return Jwts.builder()
                .setSubject(accountNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + this.TRANSACTION_EXPIRATION_TIME))
                .signWith(this.SIGNATURE_ALGORITHM, this.TRANSACTION_SECRET_KEY.getBytes())
                .compact();
    }

    public boolean validateTransactionToken(String token, String accountNumber) {
        final String accountNumberFromToken = this.extractAccountNumber(token);
        return (accountNumber.equals(accountNumberFromToken) && !this.isTransactionTokenExpired(token));
    }

    public String extractAccountNumber(String token) {
        return this.extractTransactionClaim(token, Claims::getSubject);
    }

    public <T> T extractTransactionClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractTransactionClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractTransactionClaims(String token) {
        return Jwts.parser().setSigningKey(this.TRANSACTION_SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public Date extractTransactionTokenExpiration(String token) {
        return this.extractTransactionClaim(token, Claims::getExpiration);
    }

    public boolean isTransactionTokenExpired(String token) {
        return this.extractTransactionTokenExpiration(token).before(new Date());
    }
}
