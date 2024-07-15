package com.connect.login.controller;

import com.connect.login.entity.User;
import com.connect.login.response.IncorrectPinException;
import com.connect.login.response.UserNotFoundException;
import com.connect.login.repository.UserRepository;
import com.connect.login.response.ApiResponse;
import com.connect.login.security.JwtUtil;
import com.connect.login.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserDetailsServiceImpl userDetailsService,
                          JwtUtil jwtUtil,
                          BCryptPasswordEncoder passwordEncoder,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        // Sisanya tetap sama seperti yang Anda miliki dalam metode sebelumnya
        try {
            System.out.println("Email: " + email);
            System.out.println("Password: " + password);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new IncorrectPinException("Incorrect password for email: " + email);
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, e.getMessage()));
        } catch (IncorrectPinException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, e.getMessage()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Invalid email or password"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "An error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Email is already taken!"));
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                savedUser.getPassword(),
                Collections.emptyList()
        );
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", token));
    }
}
