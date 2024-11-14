package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.statistic.VideoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Repository
public interface VideoSnapshotJpaRepository extends JpaRepository<VideoSnapshot, Long> {

    @Transactional
    @Modifying
    @Query(value = """
                INSERT INTO video_snapshot (video_id, video_view_count, advertisement_view_count, snapshot_date) 
                VALUES (:videoId, :videoViewCount, :advertisementViewCount, :snapshotDate) 
                ON CONFLICT (video_id, snapshot_date) DO UPDATE SET
                    video_view_count = video_snapshot.video_view_count + :videoViewCount,
                    advertisement_view_count = video_snapshot.advertisement_view_count + :advertisementViewCount
            """, nativeQuery = true)
    void upsertSnapshot(
            @Param("videoId") Long videoId,
            @Param("videoViewCount") Long videoViewCount,
            @Param("advertisementViewCount") Long advertisementViewCount,
            @Param("snapshotDate") LocalDate snapshotDate);
}
