package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.auth.LoginRequest;
import com.kelp_6.banking_apps.model.auth.TokenResponse;
import com.kelp_6.banking_apps.repository.UserRepository;
import com.kelp_6.banking_apps.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final ValidationService validationService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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

        return TokenResponse.builder()
                .accessToken(token)
                .build();
    }
}
