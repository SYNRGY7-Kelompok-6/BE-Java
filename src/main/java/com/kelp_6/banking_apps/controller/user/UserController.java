package com.kelp_6.banking_apps.controller.user;

import com.kelp_6.banking_apps.model.user.ProfileInfoRequest;
import com.kelp_6.banking_apps.model.user.ProfileInfoResponse;
import com.kelp_6.banking_apps.model.user.UpdateProfileInfoRequest;
import com.kelp_6.banking_apps.model.web.WebResponse;
import com.kelp_6.banking_apps.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @GetMapping({"/profile", "/profile/"})
    public WebResponse<ProfileInfoResponse> getProfile(Authentication authentication){
        LOGGER.info("accessed");

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

    @PutMapping(
            path = {"/profile", "/profile/"},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProfileInfoResponse> updateProfile(
            @Valid @ModelAttribute UpdateProfileInfoRequest request,
            @RequestParam(value = "image", required = false) MultipartFile file,
            Authentication authentication
    ){
        LOGGER.info("accessed");

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        ProfileInfoResponse profileInfoResponse = userService.updateProfile(request, file, userDetails.getUsername());

        return WebResponse.<ProfileInfoResponse>builder()
                .status("success")
                .message("profile updated successfully")
                .data(profileInfoResponse)
                .build();
    }
}
