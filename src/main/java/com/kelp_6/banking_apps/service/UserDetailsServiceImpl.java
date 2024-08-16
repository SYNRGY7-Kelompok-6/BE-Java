package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.controller.Testing;
import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("accessed");

        User user = userRepository.findByUserID(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with userID = " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(String.valueOf(user.getUserID()))
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
