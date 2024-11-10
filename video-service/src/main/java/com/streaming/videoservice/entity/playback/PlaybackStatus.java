package com.streaming.videoservice.entity.playback;

import lombok.Getter;

@Getter
public enum PlaybackStatus {

    COMPLETE("STATUS_COMPLETE"), // 영상 종료시 (어뷰징일 경우 조회수 카운트 안됨)
    PROGRESS("STATUS_PROGRESS"); // 영상 시청 시작시 (조회수 카운트 됨)

    private final String status;

    PlaybackStatus(String status) {
        this.status = status;
    }
}
