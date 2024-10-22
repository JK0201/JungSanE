package com.streaming.settlement.user.service.port;

import com.streaming.settlement.user.domain.User;
import com.streaming.settlement.user.domain.UserType;

import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmailAndUserType(String email, UserType userType);
}
