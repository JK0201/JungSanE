package com.streaming.adjustmentservice.service.port;

import com.streaming.adjustmentservice.entity.statistic.PlaybackLog;

public interface PlaybackLogRepository {
    
    void save(PlaybackLog playbackLog);
}
