package com.streaming.adjustmentservice.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonDeserialize(builder = TopVideosResponse.TopVideosResponseBuilder.class)
public class TopVideosResponse {

    private final Long videoId;
    private final Long uploaderId;
    private final Long value;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TopVideosResponseBuilder {
    }

    public static TopVideosResponse of(Long videoId, Long uploaderId, Long value) {
        return TopVideosResponse.builder()
                .videoId(videoId)
                .uploaderId(uploaderId)
                .value(value)
                .build();
    }
}
