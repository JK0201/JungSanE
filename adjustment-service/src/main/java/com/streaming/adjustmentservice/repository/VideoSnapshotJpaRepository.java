package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.VideoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoSnapshotJpaRepository extends JpaRepository<VideoSnapshot, Long> {
}
