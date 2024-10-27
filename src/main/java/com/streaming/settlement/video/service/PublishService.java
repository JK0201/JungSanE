package com.streaming.settlement.video.service;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.user.repository.UserRepository;
import com.streaming.settlement.user.security.CustomOAuth2User;
import com.streaming.settlement.video.exception.ResourceNotFoundException;
import com.streaming.settlement.video.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Slf4j(topic = "영상 업로드")
@Service
@RequiredArgsConstructor
public class PublishService {

    private final UserRepository userRepository;

    /**
     * 영상 업로드 요청 유저 객체 반환(ROLE_UPLOADER)
     *
     * @param oAuth2User (CustomOAuth2User)
     * @return User
     */
    public User findUploader(CustomOAuth2User oAuth2User) {
        boolean isUploader = hasPublishPermission(oAuth2User);
        if (!isUploader) throw new UnauthorizedAccessException("영상 업로드 권한이 없습니다.");

        // 업로드 권한이 있다면 해당 유저 DB에서 조회
        String username = oAuth2User.getUsername();
        AuthProvider authProvider = oAuth2User.getAuthProvider();

        log.info("요청 유저 = username : {}, provider : {}", username, authProvider);

        return userRepository.findByAuthProviderAndUsername(authProvider, username)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));
    }

    /**
     * 영상 업로드 요청 유저 권한 확인
     *
     * @param oAuth2User (CustomOAuth2User)
     * @return boolean
     */
    private boolean hasPublishPermission(CustomOAuth2User oAuth2User) {
        return oAuth2User.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> auth.equals("ROLE_UPLOADER") || auth.equals("ROLE_ADMIN"));
    }
}
