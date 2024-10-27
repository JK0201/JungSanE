package com.streaming.settlement.playback.repository;

import com.streaming.settlement.playback.entity.Playback;

import java.util.Optional;

public interface PlaybackRepository {

    Optional<Playback> findByUserIdAndVideoIdFetchVideo(Long userId, Long videoId);

    Playback save(Playback playback);
}
