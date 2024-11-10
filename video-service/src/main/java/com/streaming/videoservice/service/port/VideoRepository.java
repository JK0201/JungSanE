package com.streaming.videoservice.service.port;

import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.entity.VideoStatus;

import java.util.Optional;

public interface VideoRepository {

    // READ
    Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus);

    // CUD
    void save(Video video);
}
