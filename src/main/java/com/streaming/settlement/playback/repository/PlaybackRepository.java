package com.streaming.settlement.playback.repository;

import com.streaming.settlement.playback.dto.Playback;

import java.util.Optional;

public interface PlaybackRepository {

    Optional<Playback> findByUserIdAndVideoId(Long userId, Long videoId);

    Playback save(Playback playback);
}
