package com.streaming.videoservice.repository;

import com.streaming.videoservice.entity.Playback;

import java.util.Optional;

public interface PlaybackRepository {

    Optional<Playback> findByUserIdAndVideoId(Long userId, Long videoId);

    Playback save(Playback playback);
}
