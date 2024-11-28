package com.streaming.videoservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VideoStop {

    @NotNull(message = "재생 시간은 필수 입력값입니다.")
    @Min(value = 0, message = "재생 시간은 0초 이상이어야 합니다.")
    private final Long currentPosition;

    public VideoStop(Long currentPosition) {
        this.currentPosition = currentPosition;
    }
}
