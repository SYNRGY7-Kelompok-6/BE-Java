package com.connect.login.security;

import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Oauth2AccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public Map<String, ?> convertAccessToken(org.springframework.security.oauth2.common.OAuth2AccessToken token, org.springframework.security.oauth2.provider.OAuth2Authentication authentication) {
        Map<String, Object> response = (Map<String, Object>) super.convertAccessToken(token, authentication);
        response.put("username", authentication.getName());
        return response;
    }
}
