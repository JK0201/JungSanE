package com.streaming.gatewayservice.response;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class ResponseHandler {

    /**
     * @param response (ServerHttpResponse)
     * @param message  (String)
     * @return 400 Not Found 응답
     */
    public static Mono<Void> notFound(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = response.bufferFactory()
                .wrap(createBody(message, HttpStatus.NOT_FOUND.toString())
                        .getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * @param response (ServerHttpResponse)
     * @param message  (String)
     * @return 401 Unauthorized 응답
     */
    public static Mono<Void> unauthorized(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = response.bufferFactory()
                .wrap(createBody(message, HttpStatus.UNAUTHORIZED.toString())
                        .getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    /**
     * @param response (ServerHttpResponse)
     * @param message  (String)
     * @return 403 Forbidden 응답
     */
    public static Mono<Void> forbidden(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = response.bufferFactory()
                .wrap(createBody(message, HttpStatus.FORBIDDEN.toString())
                        .getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    private static String createBody(String message, String code) {
        return String.format("{\"code\": \"%s\", \"message\": \"%s\"}", code, message);
    }
}
