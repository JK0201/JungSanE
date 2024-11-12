package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.PlaybackLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaybackLogJpaRepository extends JpaRepository<PlaybackLog, Long> {
}
