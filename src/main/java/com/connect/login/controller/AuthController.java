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
    public ResponseEntity<ApiResponse<String>> getUserByUsername(@RequestParam("username") String username,
                                                                 @RequestParam("pin") String pin) {
        try {
            System.out.println("Username: " + username);
            System.out.println("PIN: " + pin);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(pin, userDetails.getPassword())) {
                throw new IncorrectPinException("Incorrect PIN for username: " + username);
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, pin));
            String token = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", token));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(new ApiResponse<>(false, e.getMessage()));
        } catch (IncorrectPinException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, e.getMessage()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Invalid username or pin"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "An error occurred: " + e.getMessage()));
        }
    }

    @PostMapping("/register") // sementara
    public ResponseEntity<ApiResponse<String>> registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Username is already taken!"));
        }
        String encodedPin = passwordEncoder.encode(user.getPin());
        user.setPin(encodedPin);
        User savedUser = userRepository.save(user);

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                savedUser.getUsername(),
                savedUser.getPin(),
                Collections.emptyList()
        );
        String token = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", token));
    }
}
