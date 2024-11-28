package com.streaming.videoservice.service;

import com.streaming.videoservice.controller.port.VideoCommandService;
import com.streaming.videoservice.dto.request.VideoPublish;
import com.streaming.videoservice.entity.video.Video;
import com.streaming.videoservice.service.port.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VideoCommandServiceImpl implements VideoCommandService {

    private final VideoRepository videoRepository;

    /**
     * 영상 업로드 (특정 권한만 가능)
     * gateway-service에서 토큰 권한 검증 후, 권한 여부에 따라 실행 가능
     *
     * @param videoPublish (VideoPublish)
     * @param userId       (Long)
     */
    public void publish(VideoPublish videoPublish, Long userId) {
        Video video = Video.from(videoPublish, userId);
        videoRepository.save(video);
    }
}
