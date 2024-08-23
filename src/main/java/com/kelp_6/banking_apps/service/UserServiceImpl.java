package com.kelp_6.banking_apps.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.model.user.ProfileInfoRequest;
import com.kelp_6.banking_apps.model.user.ProfileInfoResponse;
import com.kelp_6.banking_apps.model.user.UpdateProfileInfoRequest;
import com.kelp_6.banking_apps.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService{
    private final static Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
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
        LOGGER.info("accessed");

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
        LOGGER.info("accessed");

        User user = userRepository.findByUserID(userID).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        String imageUrl = user.getImageUrl();
        if (file != null) {
            // Validate the content type
            String contentType = file.getContentType();
            if (contentType == null || !isImage(contentType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only image files are allowed");
            }
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                imageUrl = (String) uploadResult.get("url");
            }catch (IOException exception){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
            }
        }

        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getUsername()) && userRepository.existsByUsername(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email already exists");
        }

        user.setImageUrl(imageUrl);
        user.setName((!StringUtils.hasText(request.getName())) ? user.getName() : request.getName());
        user.setUsername((!StringUtils.hasText(request.getEmail())) ? user.getUsername() : request.getEmail());
        user.setPhone((!StringUtils.hasText(request.getPhone())) ? user.getPhone() : request.getPhone());
        user.setBirth((request.getBirth() == null) ? user.getBirth() : request.getBirth());
        user.setAddress((!StringUtils.hasText(request.getAddress())) ? user.getAddress() : request.getAddress());

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

    private boolean isImage(String contentType) {
        LOGGER.info("accessed");

        return contentType.equals("image/png") ||
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/gif");
    }
}
