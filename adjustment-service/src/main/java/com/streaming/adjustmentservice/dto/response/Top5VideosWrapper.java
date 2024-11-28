package com.streaming.adjustmentservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Top5VideosWrapper {

    private final Long videoId;
    private final Long uploaderId;
    private final Long value;

    public static Top5VideosWrapper of(Long videoId, Long uploaderId, Long value) {
        return Top5VideosWrapper.builder()
                .videoId(videoId)
                .uploaderId(uploaderId)
                .value(value)
                .build();
    }
}
