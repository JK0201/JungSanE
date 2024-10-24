package com.streaming.settlement.user.dto;

import com.streaming.settlement.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final User user;

    // Provider에 따라 Response 형태가 틀리기 떄문에 아래에 직접 getter 작성
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // role.getAuthority(String - ROLE_USER, ROLE_UPLOADER)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        UserRole role = user.getRole();
        String authority = role.getAuthority();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(simpleGrantedAuthority);

        return authorities;
    }

    @Override
    public String getName() {
        return user.getNickname();
    }

    // 고유 아이디값 (Provider + ProviderId)
    public String getUsername() {
        return user.getUsername();
    }
}
