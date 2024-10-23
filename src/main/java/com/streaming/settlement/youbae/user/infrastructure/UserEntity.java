//package com.streaming.settlement.user.infrastructure;
//
//import com.streaming.settlement.common.infrastructure.BaseEntity;
//import com.streaming.settlement.user.domain.User;
//import com.streaming.settlement.user.domain.UserRole;
//import com.streaming.settlement.user.domain.UserType;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@Table(name = "users")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class UserEntity extends BaseEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String email;
//
//    @Column(nullable = false)
//    private String username;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private UserRole role;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private UserType userType;
//
//    public static UserEntity from(User user) {
//        UserEntity userEntity = new UserEntity();
//        userEntity.id = user.getId();
//        userEntity.email = user.getEmail();
//        userEntity.username = user.getUsername();
//        userEntity.role = user.getRole();
//        userEntity.userType = user.getUserType();
//
//        return userEntity;
//    }
//
//    public User toModel() {
//        return User.builder()
//                .id(id)
//                .email(email)
//                .username(username)
//                .role(role)
//                .userType(userType)
//                .build();
//    }
//}
