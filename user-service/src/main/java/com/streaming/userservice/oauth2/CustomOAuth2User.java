package com.streaming.userservice.oauth2;

import com.streaming.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;

    // Provider에 따라 Response 형태가 틀리기 떄문에 아래에 직접 getter 작성
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // UserRole.getAuthority()
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().getAuthority()));
    }

    // User Id (String)
    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }
}
