package com.kelp_6.banking_apps.controller.user;

import com.kelp_6.banking_apps.model.user.ProfileInfoRequest;
import com.kelp_6.banking_apps.model.user.ProfileInfoResponse;
import com.kelp_6.banking_apps.model.user.UpdateProfileInfoRequest;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping({"/profile", "/profile/"})
    public WebResponse<ProfileInfoResponse> getProfile(Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        ProfileInfoRequest request = ProfileInfoRequest.builder()
                .userID(userDetails.getUsername())
                .build();

        ProfileInfoResponse profileInfoResponse = userService.getProfile(request);

        return WebResponse.<ProfileInfoResponse>builder()
                .status("success")
                .message("profile retrieved successfully")
                .data(profileInfoResponse)
                .build();
    }

    @PutMapping({"/profile", "/profile/"})
    public WebResponse<ProfileInfoResponse> updateProfile(
            @Valid @ModelAttribute UpdateProfileInfoRequest request,
            @RequestParam(value = "image", required = false) MultipartFile file,
            Authentication authentication
    ){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        ProfileInfoResponse profileInfoResponse = userService.updateProfile(request, file, userDetails.getUsername());

        return WebResponse.<ProfileInfoResponse>builder()
                .status("success")
                .message("profile updated successfully")
                .data(profileInfoResponse)
                .build();
    }
}
