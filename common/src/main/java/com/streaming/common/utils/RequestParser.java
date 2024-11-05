package com.streaming.common.utils;

import com.streaming.common.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;

public class RequestParser {

    public Long extractUserIdFromHeader(HttpServletRequest request) {
        String userIdFromHeader = request.getHeader("");
        if (userIdFromHeader == null) {
            throw new UnauthorizedAccessException("");
        }

        Long userId = Long.parseLong(userIdFromHeader);
        return userId;
    }
}
