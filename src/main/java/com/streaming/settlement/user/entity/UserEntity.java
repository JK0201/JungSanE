package com.streaming.settlement.user.entity;

import com.streaming.settlement.user.dto.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.id = user.getId();
        userEntity.email = user.getEmail();
        userEntity.username = user.getUsername();
        userEntity.nickname = user.getNickname();
        userEntity.role = user.getRole();
        userEntity.authProvider = user.getAuthProvider();
        userEntity.createdAt = user.getCreatedAt();
        userEntity.modifiedAt = user.getModifiedAt();

        return userEntity;
    }

    public User toModel() {
        return User.builder()
                .id(id)
                .email(email)
                .username(username)
                .nickname(nickname)
                .role(role)
                .authProvider(authProvider)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }
}
