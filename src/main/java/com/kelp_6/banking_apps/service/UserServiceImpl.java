package com.kelp_6.banking_apps.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.user.ProfileInfoRequest;
import com.kelp_6.banking_apps.model.user.ProfileInfoResponse;
import com.kelp_6.banking_apps.model.user.UpdateProfileInfoRequest;
import com.kelp_6.banking_apps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;
    private final SimpleDateFormat formatter;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            Cloudinary cloudinary,
            @Qualifier("birthDateFormat") SimpleDateFormat formatter
    ) {
        this.userRepository = userRepository;
        this.cloudinary = cloudinary;
        this.formatter = formatter;
    }

    @Override
    public ProfileInfoResponse getProfile(ProfileInfoRequest request) {
        User user = userRepository.findByUserID(request.getUserID()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        return ProfileInfoResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .address(user.getAddress())
                .birth(formatter.format(user.getBirth()))
                .email(user.getUsername())
                .imageUrl(user.getImageUrl())
                .build();
    }

    @Override
    @Transactional
    public ProfileInfoResponse updateProfile(UpdateProfileInfoRequest request, MultipartFile file, String userID) {
        User user = userRepository.findByUserID(userID).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        String imageUrl = user.getImageUrl();
        if(file != null){
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                imageUrl = (String) uploadResult.get("url");
            }catch (IOException exception){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
            }
        }

        if(request.getEmail() != null && userRepository.existsByUsername(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email already exists");
        }

        user.setImageUrl(imageUrl);
        user.setName((request.getName() == null) ? user.getName() : request.getName());
        user.setUsername((request.getEmail() == null) ? user.getUsername() : request.getEmail());
        user.setPhone((request.getPhone() == null) ? user.getPhone() : request.getPhone());
        user.setBirth((request.getBirth() == null) ? user.getBirth() : request.getBirth());
        user.setAddress((request.getAddress() == null) ? user.getAddress() : request.getAddress());

        userRepository.save(user);

        return ProfileInfoResponse.builder()
                .name(user.getName())
                .email(user.getUsername())
                .phone(user.getPhone())
                .birth(formatter.format(user.getBirth()))
                .address(user.getAddress())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
