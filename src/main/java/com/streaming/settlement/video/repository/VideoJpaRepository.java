package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.entity.VideoEntity;
import com.streaming.settlement.video.entity.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoJpaRepository extends JpaRepository<VideoEntity, Long> {

    Optional<VideoEntity> findByIdAndStatus(Long videoId, VideoStatus videoStatus);
}
