package com.streaming.videoservice.repository;

import com.streaming.videoservice.entity.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackJpaRepository extends JpaRepository<Playback, Long> {

    @Query("select p from Playback p " +
            "where p.userId = :userId " +
            "and p.video.id = :videoId " +
            "order by p.createdAt " +
            "desc limit 1")
    Optional<Playback> findByUserIdAndVideoId(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);
}