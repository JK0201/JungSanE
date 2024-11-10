package com.streaming.videoservice.repository.video;

import com.streaming.videoservice.entity.video.Video;
import com.streaming.videoservice.entity.video.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoJpaRepository extends JpaRepository<Video, Long> {

    // READ
    Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus);

    // CUD
}
