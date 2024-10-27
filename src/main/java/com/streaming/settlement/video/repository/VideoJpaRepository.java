package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.entity.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoJpaRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus);
}
