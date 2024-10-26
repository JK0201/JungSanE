package com.streaming.settlement.playback.repository;

import com.streaming.settlement.playback.entity.PlaybackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaybackJpaRepository extends JpaRepository<PlaybackEntity, Long> {

    @Query("select p from PlaybackEntity p " +
            "where p.userEntity.id = :userId " +
            "and p.videoEntity.id = :videoId")
    Optional<PlaybackEntity> findByUserIdAndVideoId(
            @Param("userId") Long userId,
            @Param("videoId") Long videoId);
}
