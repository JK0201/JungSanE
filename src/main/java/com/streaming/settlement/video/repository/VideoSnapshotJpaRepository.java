package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.entity.VideoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoSnapshotJpaRepository extends JpaRepository<VideoSnapshot, Long> {
}
