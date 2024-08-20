package com.kelp_6.banking_apps.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kelp_6.banking_apps.security.util.JwtUtil;
import com.kelp_6.banking_apps.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final ObjectMapper objectMapper;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final HashSet<String> withoutJwtUrls = new HashSet<>(
            Arrays.asList(
                    "/api/v1.0/auth/login",
                    "/api/v1.0/ping",
                    "/api/v1.0/financial-calculator",
                    "/api/v1.0",
                    "/api/v1.0/swagger-ui/index.html")
    );
    private final HashSet<String> swaggerUrls = new HashSet<>(
            Arrays.asList(
                    "/api/v1.0/swagger-ui/index.html",
                    "/api/v1.0/swagger-resources",
                    "/api/v1.0/v2/api-docs",
                    "/api/v1.0/webjars",
                    "/api/v1.0/swaggerfox.js",
                    "/api/v1.0/swagger-ui",
                    "/api/v1.0/v3/api-docs"
            )
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain)
            throws ServletException, IOException {
        log.info(request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (this.withoutJwtUrls.contains(request.getRequestURI())
                    || this.swaggerUrls.stream().anyMatch(s -> request.getRequestURI().contains(s))) {
                chain.doFilter(request, response);
                return;
            }

            Map<String, Object> errorResponse = new LinkedHashMap<>();
            errorResponse.put("status", "UNAUTHORIZED");
            errorResponse.put("message", "invalid authorization");
            errorResponse.put("data", null);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            this.objectMapper.writeValue(response.getWriter(), errorResponse);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userID = this.jwtUtil.extractUsername(jwt);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userID != null && authentication == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userID);
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}