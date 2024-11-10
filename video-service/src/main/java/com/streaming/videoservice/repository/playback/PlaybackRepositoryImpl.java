package com.streaming.videoservice.repository.playback;

import com.streaming.videoservice.entity.Playback;
import com.streaming.videoservice.service.port.PlaybackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlaybackRepositoryImpl implements PlaybackRepository {

    private final PlaybackJpaRepository playbackJpaRepository;

    // READ
    @Override
    public Optional<Playback> findByUserIdAndVideoIdOnStart(Long userId, Long videoId) {
        return playbackJpaRepository.findByUserIdAndVideoIdOnStart(userId, videoId);
    }

    @Override
    public Optional<Playback> findByUserIdAndVideoIdOnStop(Long userId, Long videoId) {
        return playbackJpaRepository.findByUserIdAndVideoIdOnStop(userId, videoId);
    }

    // CUD
    @Override
    public Playback save(Playback playback) {
        return playbackJpaRepository.save(playback);
    }
}
