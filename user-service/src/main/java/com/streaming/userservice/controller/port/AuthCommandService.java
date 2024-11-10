package com.streaming.userservice.controller.port;

import com.streaming.userservice.dto.response.TokenWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AuthCommandService {

    TokenWrapper reissueToken(String refreshToken);

    void logout(String refreshToken, HttpServletResponse response);
}
