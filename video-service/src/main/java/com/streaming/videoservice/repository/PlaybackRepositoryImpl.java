package com.streaming.videoservice.repository;

import com.streaming.videoservice.entity.Playback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlaybackRepositoryImpl implements PlaybackRepository {

    private final PlaybackJpaRepository playbackJpaRepository;

    @Override
    public Optional<Playback> findByUserIdAndVideoId(Long userId, Long videoId) {
        return playbackJpaRepository.findByUserIdAndVideoId(userId, videoId);
    }

    @Override
    public Playback save(Playback playback) {
        return playbackJpaRepository.save(playback);
    }
}
