package com.streaming.settlement.user.entity;

import com.streaming.settlement.user.dto.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

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

    @OneToMany(mappedBy = "refreshToken", orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokenList = new ArrayList<>();

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.id = user.getId();
        userEntity.email = user.getEmail();
        userEntity.username = user.getUsername();
        userEntity.nickname = user.getNickname();
        userEntity.role = user.getRole();

        return userEntity;
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .email(email)
                .username(username)
                .nickname(nickname)
                .role(role)
                .build();
    }
}
