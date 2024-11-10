package com.streaming.videoservice.entity.video;

public enum VideoStatus {
    ACTIVE("STATUS_ACTIVE"), // 시청 가능 동영상 (공개)
    INACTIVE("STATUS_INACTIVE"), // 시청 불가 동영상 (비공개)
    DELETED("STATUS_DELETED"); // 삭제된 동영상

    private final String status;

    VideoStatus(String status) {
        this.status = status;
    }
}
