package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.dto.Video;
import com.streaming.settlement.video.entity.VideoStatus;

import java.util.Optional;

public interface VideoRepository {

    Video save(Video video);

    Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus);
}
