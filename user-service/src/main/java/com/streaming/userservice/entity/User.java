package com.streaming.userservice.entity;

import com.streaming.common.entity.Timestamped;
import com.streaming.userservice.dto.OAuth2Response;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

//    public static User from() {
//        User user = new User();
//        user.id = user.getId();
//        user.email = user.getEmail();
//        user.username = user.getUsername();
//        user.nickname = user.getNickname();
//        user.role = user.getRole();
//        user.authProvider = user.getAuthProvider();
//        return user;
//    }

    public static User fromOAuth(String username, OAuth2Response oAuth2Response, AuthProvider authProvider) {
        User user = new User();
        user.email = oAuth2Response.getEmail();
        user.username = username;
        user.nickname = oAuth2Response.getName();
        user.role = UserRole.UPLOADER; // FIXME 테스트 후 권한 설정 변경
        user.authProvider = authProvider;
        return user;
    }

    public static User fromToken(String username, UserRole userRole, AuthProvider authProvider) {
        User user = new User();
        user.username = username;
        user.role = userRole;
        user.authProvider = authProvider;
        return user;
    }
}
