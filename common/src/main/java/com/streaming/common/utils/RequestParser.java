package com.streaming.common.utils;

import com.streaming.common.exception.UnauthorizedAccessException;
import jakarta.servlet.http.HttpServletRequest;

public class RequestParser {

    /**
     * API Gateway에서 넘어온 userId (String) -> (Long)으로 파싱
     *
     * @param request (HttpServletRequest)
     * @return userId (Long)
     */
    public static Long extractUserIdFromHeader(HttpServletRequest request) {
        String userIdFromHeader = request.getHeader("X-User-Id");

        if (userIdFromHeader == null) {
            throw new UnauthorizedAccessException("헤더에 사용자 ID가 없습니다.");
        }

        try {
            return Long.parseLong(userIdFromHeader);
        } catch (NumberFormatException ex) {
            throw new UnauthorizedAccessException("잘못된 형식의 사용자 ID 입니다.");
        }
    }
}
