package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.LoginInfos;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.repository.LoginInfosRepository;
import com.kelp_6.banking_apps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService{
    private final UserRepository userRepository;
    private final LoginInfosRepository loginInfosRepository;

    @Override
    public void logFailedAttempt(String userID, String ipAddress) {
        User user = userRepository.findByUserID(userID).orElse(null);

        if(user == null) return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
        calendar.setTime(new Date());

        LoginInfos loginInfos = LoginInfos.builder()
                .user(user)
                .ipAddress(ipAddress)
                .timestamp(calendar.getTime())
                .isSuccess(false)
                .location("Bekasi, Indonesia")
                .build();

        loginInfosRepository.save(loginInfos);
    }
}
