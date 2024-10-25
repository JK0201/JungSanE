package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.dto.Video;

public interface VideoCommandRepository {

    Video save(Video video);
}
