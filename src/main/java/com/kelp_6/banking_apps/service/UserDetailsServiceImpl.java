package com.kelp_6.banking_apps.service;

import com.kelp_6.banking_apps.entity.User;
import com.kelp_6.banking_apps.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserID(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found with userID = " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(String.valueOf(user.getUserID()))
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}
