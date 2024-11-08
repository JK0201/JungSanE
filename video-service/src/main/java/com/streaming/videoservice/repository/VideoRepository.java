package com.streaming.videoservice.repository;

import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.entity.VideoStatus;

import java.util.Optional;

public interface VideoRepository {

    Video save(Video video);

    Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus);
}
