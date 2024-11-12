package com.streaming.adjustmentservice.service;

import com.streaming.adjustmentservice.entity.PlaybackLog;
import com.streaming.adjustmentservice.service.port.PlaybackLogRepository;
import com.streaming.common.dto.event.PlaybackEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlaybackEventConsumer {

    private final PlaybackLogRepository playbackLogRepository;
    
    @KafkaListener(groupId = "adjustment-service", topics = "playback-logs")
    public void consumePlaybackEvent(PlaybackEvent event) {
        log.info("Received event - videoId: {}, uploaderId: {}, playTime: {}, adCount: {}",
                event.getVideoId(),
                event.getUploaderId(),
                event.getVideoPlayedTime(),
                event.getAdvertisementViewCount()
        );

        PlaybackLog playbackLog = PlaybackLog.from(event);
        playbackLogRepository.save(playbackLog);
    }
}
