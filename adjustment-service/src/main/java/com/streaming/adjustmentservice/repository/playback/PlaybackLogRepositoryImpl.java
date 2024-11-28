package com.streaming.adjustmentservice.repository.playback;

import com.streaming.adjustmentservice.entity.statistic.PlaybackLog;
import com.streaming.adjustmentservice.service.port.PlaybackLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PlaybackLogRepositoryImpl implements PlaybackLogRepository {

    private final PlaybackLogJpaRepository playbackLogJpaRepository;

    @Override
    public void save(PlaybackLog playbackLog) {
        playbackLogJpaRepository.save(playbackLog);
    }
}
