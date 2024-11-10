package com.streaming.videoservice.service.port;

import com.streaming.videoservice.entity.playback.Playback;

import java.util.Optional;

public interface PlaybackRepository {

    // READ
    Optional<Playback> findByUserIdAndVideoIdOnStart(Long userId, Long videoId);

    Optional<Playback> findByUserIdAndVideoIdOnStop(Long userId, Long videoId);

    // CUD
    Playback save(Playback playback);
}
