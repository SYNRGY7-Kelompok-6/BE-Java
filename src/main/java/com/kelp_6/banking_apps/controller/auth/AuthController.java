package com.kelp_6.banking_apps.controller.auth;

import com.kelp_6.banking_apps.model.auth.LoginInfoResponse;
import com.kelp_6.banking_apps.model.auth.LoginRequest;
import com.kelp_6.banking_apps.model.auth.PinValidationRequest;
import com.kelp_6.banking_apps.model.auth.TokenResponse;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping({"/login", "/login/"})
    public ResponseEntity<WebResponse<TokenResponse>> login(@RequestBody LoginRequest bodyRequest, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        bodyRequest.setIpAddress(ipAddress);

        TokenResponse tokenResponse = this.authService.login(bodyRequest);
        WebResponse<TokenResponse> response = WebResponse.<TokenResponse>builder()
                .status("success")
                .message("login success")
                .data(tokenResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping({"/login-info", "login-info/"})
    public ResponseEntity<WebResponse<LoginInfoResponse>> loginInfo(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        LoginInfoResponse loginInfoResponse = this.authService.getLoginInfo(userDetails.getUsername());
        WebResponse<LoginInfoResponse> response = WebResponse.<LoginInfoResponse>builder()
                .status("success")
                .message("success getting login information")
                .data(loginInfoResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping({"/validate-pin", "/validate-pin/"})
    public ResponseEntity<WebResponse<Void>> validatePin(@RequestBody PinValidationRequest pinValidationRequest) {
        boolean isValid = authService.validatePin(pinValidationRequest);

        WebResponse<Void> response = WebResponse.<Void>builder()
                .status("success")
                .message("pin valid")
                .data(null)
                .build();
        //belum return token baru
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
