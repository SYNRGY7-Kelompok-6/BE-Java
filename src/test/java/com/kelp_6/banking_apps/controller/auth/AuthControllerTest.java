package com.kelp_6.banking_apps.controller.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

import com.kelp_6.banking_apps.model.auth.*;
import com.kelp_6.banking_apps.service.AuthService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("testuser", "password", "ip");

        TokenResponse tokenResponse = new TokenResponse("test-token");

        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        // Act & Assert
        MvcResult response = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.message", is("login success")))
                .andExpect(jsonPath("$.data.accessToken", is("test-token")))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), response.getResponse().getStatus());

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void testLoginInfo() throws Exception {
        UserDetails userDetails = User.withUsername("testUser").password("password").authorities("USER").build();
        when(authentication.getPrincipal()).thenReturn(userDetails);

        LoginInfoResponse loginInfoResponse = LoginInfoResponse.builder()
                .lastSuccessfullLoginAttempt(DetailLoginInfoResponse.builder()
                        .timestamp("2024-08-01 07:12:46.458")
                        .location("Bekasi, Indonesia")
                        .build())
                .failedLoginAttempt(DetailLoginInfoResponse.builder()
                        .timestamp("2024-07-30 20:06:58.057")
                        .location("Surabaya, Indonesia")
                        .build())
                .build();

        when(authService.getLoginInfo(anyString())).thenReturn(loginInfoResponse);

        mockMvc.perform(get("/auth/login-info")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("success getting login information"))
                .andExpect(jsonPath("$.data.lastSuccessfullLoginAttempt.timestamp").value("2024-08-01 07:12:46.458"))
                .andExpect(jsonPath("$.data.lastSuccessfullLoginAttempt.location").value("Bekasi, Indonesia"))
                .andExpect(jsonPath("$.data.failedLoginAttempt.timestamp").value("2024-07-30 20:06:58.057"))
                .andExpect(jsonPath("$.data.failedLoginAttempt.location").value("Surabaya, Indonesia"));
    }

    @Test
    void testValidatePin() throws Exception {
        // Create a sample PinValidationRequest
        PinValidationRequest pinValidationRequest = new PinValidationRequest();
        pinValidationRequest.setPin("123456");
        pinValidationRequest.setUserId("testUserId");

        // Create a sample PinTokenResponse
        PinTokenResponse pinTokenResponse = PinTokenResponse.builder()
                .pinToken("sampleToken")
                .build();

        // Mock the authService.validatePin method
        when(authService.validatePin(any(PinValidationRequest.class))).thenReturn(pinTokenResponse);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/auth/validate-pin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pinValidationRequest)))
                .andDo(print()) // Print the response for debugging
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("pin valid"))
                .andExpect(jsonPath("$.data.pinToken").value("sampleToken"));
    }
}