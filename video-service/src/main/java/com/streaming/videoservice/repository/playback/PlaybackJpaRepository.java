package com.streaming.videoservice.repository.playback;

import com.streaming.videoservice.entity.playback.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackJpaRepository extends JpaRepository<Playback, Long> {

    // READ
    @Query("SELECT p FROM Playback p " +
            "JOIN FETCH p.video v " +
            "WHERE p.userId = :userId " +
            "AND p.video.id = :videoId " +
            "AND v.status = 'ACTIVE'")
    Optional<Playback> findByUserIdAndVideoIdOnStart(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);

    @Query("SELECT p FROM Playback p " +
            "JOIN FETCH p.video v " +
            "LEFT JOIN FETCH v.videoAdvertisementList " +
            "WHERE p.userId = :userId " +
            "AND p.video.id = :videoId " +
            "AND v.status = 'ACTIVE'")
    Optional<Playback> findByUserIdAndVideoIdOnStop(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);

    // CUD
}