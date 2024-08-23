package com.kelp_6.banking_apps.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelp_6.banking_apps.model.web.WebResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/auth/login").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                            WebResponse<Void> errorResponse = WebResponse.<Void>builder()
                                    .status("error")
                                    .message("Invalid credentials")
                                    .build();

                            ObjectMapper mapper = new ObjectMapper();
                            mapper.writeValue(response.getOutputStream(), errorResponse);
                        })
                );
        return http.build();
    }
}