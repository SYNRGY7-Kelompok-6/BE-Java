package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.model.user.ProfileInfoRequest;
import com.kelp_6.banking_apps.model.user.ProfileInfoResponse;
import com.kelp_6.banking_apps.model.user.UpdateProfileInfoRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    ProfileInfoResponse getProfile(ProfileInfoRequest request);
    ProfileInfoResponse updateProfile(UpdateProfileInfoRequest request, MultipartFile file, String userID);
}
