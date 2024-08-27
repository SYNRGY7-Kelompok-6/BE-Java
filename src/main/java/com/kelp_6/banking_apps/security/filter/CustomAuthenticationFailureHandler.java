package com.kelp_6.banking_apps.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.LoginAttemptService;
import com.kelp_6.banking_apps.service.UserDetailsServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final LoginAttemptService loginAttemptService;
    private final ObjectMapper objectMapper;
    private final static Logger LOGGER = LoggerFactory.getLogger(CustomAuthenticationFailureHandler.class);

    public CustomAuthenticationFailureHandler(LoginAttemptService loginAttemptService, ObjectMapper objectMapper) {
        this.loginAttemptService = loginAttemptService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        LOGGER.info("Authentication failed for user ID: {}", extractUserID(request));
        String ipAddress = request.getRemoteAddr();
        String userID = extractUserID(request);
        if(userID != null) loginAttemptService.logFailedAttempt(userID, ipAddress);

        // Construct a custom error response
        WebResponse<Object> errResponse = WebResponse
                .<Object>builder()
                .status(HttpStatus.UNAUTHORIZED.toString())
                .message("Bad credentials")
                .data(null)
                .build();

        // Send the response
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errResponse));
    }

    private String extractUserID(HttpServletRequest request){
        try{
            Map loginRequest = objectMapper.readValue(request.getInputStream(), Map.class);
            return (String) loginRequest.get("userID");
        }catch (IOException exception){
            return null;
        }
    }
}
