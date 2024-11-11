package com.streaming.adjustmentservice.service;

import com.streaming.common.dto.event.PlaybackEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PlaybackEventConsumer {
    
    @KafkaListener(topics = "playback-logs", groupId = "adjustment-service")
    public void consume(PlaybackEvent event) {
        log.info("Received event - videoId: {}, uploaderId: {}, playTime: {}, adCount: {}",
                event.getVideoId(),
                event.getUploaderId(),
                event.getVideoPlayedTime(),
                event.getAdvertisementViewCount()
        );
    }
}
