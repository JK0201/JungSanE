package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.entity.VideoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoJpaCommandRepository extends JpaRepository<VideoEntity, Long> {

}
