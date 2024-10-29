package com.streaming.settlement.playback.repository;

import com.streaming.settlement.playback.entity.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackJpaRepository extends JpaRepository<Playback, Long> {

    @Query("select p from Playback p " +
            "where p.user.id = :userId " +
            "and p.video.id = :videoId " +
            "order by p.createdAt " +
            "desc limit 1")
    Optional<Playback> findByUserIdAndVideoId(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);
}