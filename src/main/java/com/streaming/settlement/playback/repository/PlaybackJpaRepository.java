package com.streaming.settlement.playback.repository;

import com.streaming.settlement.playback.entity.Playback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackJpaRepository extends JpaRepository<Playback, Long> {

    @Query("select p from Playback p " +
            "join fetch p.video v " +
            "where p.user.id = :userId " +
            "and p.video.id = :videoId")
    Optional<Playback> findByUserIdAndVideoIdFetchVideo(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);
}