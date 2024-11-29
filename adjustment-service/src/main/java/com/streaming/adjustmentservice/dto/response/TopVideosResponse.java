package com.streaming.adjustmentservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TopVideosResponse {

    private final Long videoId;
    private final Long uploaderId;
    private final Long value;

    public static TopVideosResponse of(Long videoId, Long uploaderId, Long value) {
        return TopVideosResponse.builder()
                .videoId(videoId)
                .uploaderId(uploaderId)
                .value(value)
                .build();
    }
}
