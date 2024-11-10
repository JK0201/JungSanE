package com.streaming.videoservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VideoPublish {

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "영상에 대한 설명을 입력해주세요.")
    private final String description;

    @NotNull(message = "재생 시간은 필수 입력값입니다.")
    @Min(value = 0, message = "재생 시간은 0초 이상이어야 합니다.")
    private final Long playbackTime;

    public VideoPublish(String title, String description, Long playbackTime) {
        this.title = title;
        this.description = description;
        this.playbackTime = playbackTime;
    }
}
