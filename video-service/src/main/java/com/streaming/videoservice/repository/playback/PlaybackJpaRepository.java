package com.streaming.videoservice.repository.playback;

import com.streaming.videoservice.entity.playback.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackJpaRepository extends JpaRepository<Playback, Long> {

    // READ
    @Query("select p from Playback p " +
            "join fetch p.video v " +
            "where p.userId = :userId " +
            "and p.video.id = :videoId " +
            "and v.status = 'ACTIVE'")
    Optional<Playback> findByUserIdAndVideoIdOnStart(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);

    @Query("select p from Playback p " +
            "join fetch p.video v " +
            "left join fetch v.videoAdvertisementList " +
            "where p.userId = :userId " +
            "and p.video.id = :videoId " +
            "and v.status = 'ACTIVE'")
    Optional<Playback> findByUserIdAndVideoIdOnStop(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);

    // CUD
}