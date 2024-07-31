package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.LoginInfos;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.auth.DetailLoginInfoResponse;
import com.kelp_6.banking_apps.model.auth.LoginInfoResponse;
import com.kelp_6.banking_apps.model.auth.LoginRequest;
import com.kelp_6.banking_apps.model.auth.TokenResponse;
import com.kelp_6.banking_apps.model.auth.*;
import com.kelp_6.banking_apps.repository.LoginInfosRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final LoginInfosRepository loginInfosRepository;
    private final ValidationService validationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public TokenResponse login(LoginRequest request) {
        this.validationService.validate(request);

        User user = this.userRepository
                .findByUserID(request.getUserID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUserID(),
                request.getPassword()
        ));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUserID())
                .password(user.getPassword())
                .authorities("USER")
                .build();
        String token = this.jwtUtil.generateToken(userDetails);

        LoginInfos loginInfos = LoginInfos.builder()
                .user(user)
                .ipAddress(request.getIpAddress())
                .timestamp(new Date())
                .isSuccess(true)
                .location("Bekasi, Indonesia")
                .build();

        loginInfosRepository.save(loginInfos);

        return TokenResponse.builder()
                .accessToken(token)
                .build();
    }

    @Override
    public LoginInfoResponse getLoginInfo(String userID) {
        LoginInfos loginInfosSuccess = loginInfosRepository.findLoginSuccessByUser_UserID(userID).orElse(null);
        LoginInfos loginInfosFailed = loginInfosRepository.findLoginFailedByUser_UserID(userID).orElse(null);

        DetailLoginInfoResponse successfulLoginAttempt = DetailLoginInfoResponse.builder()
                .timestamp(loginInfosSuccess != null ? loginInfosSuccess.getTimestamp().toString() : null)
                .location(loginInfosSuccess != null ? loginInfosSuccess.getLocation() : null)
                .build();

        DetailLoginInfoResponse failedLoginAttempt = DetailLoginInfoResponse.builder()
                .timestamp(loginInfosFailed != null ? loginInfosFailed.getTimestamp().toString() : null)
                .location(loginInfosFailed != null ? loginInfosFailed.getLocation() : null)
                .build();

        return LoginInfoResponse.builder()
                .lastSuccessfullLoginAttempt(successfulLoginAttempt)
                .failedLoginAttempt(failedLoginAttempt)
                .build();
    }
    @Override
    public PinTokenResponse validatePin(PinValidationRequest pinValidationRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        logger.info("Authenticating user with ID: {}", userId);

        User user = userRepository.findByUserID(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found"));
        logger.info("User found: {}", user.getName());

        if (passwordEncoder.matches(pinValidationRequest.getPin(), user.getPin())) {
            String accountNumber = user.getAccount().getAccountNumber();
            String transactionToken = jwtUtil.generateTransactionToken(accountNumber);
            return PinTokenResponse.builder()
                    .pinToken(transactionToken)
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid PIN");
        }
    }
}
