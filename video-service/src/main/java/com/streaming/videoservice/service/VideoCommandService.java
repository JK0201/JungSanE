package com.streaming.videoservice.service;

import com.streaming.videoservice.dto.PlaybackResponse;
import com.streaming.videoservice.dto.StopRequest;
import com.streaming.videoservice.dto.VideoPublish;
import com.streaming.videoservice.dto.VideoResponse;
import com.streaming.videoservice.entity.Playback;
import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoCommandService {

    private final VideoRepository videoRepository;
    private final UserPlaybackService userPlaybackService;

    /**
     * 영상 업로드
     *
     * @param videoPublish (VideoPublish)
     * @param userId       (Long)
     * @return VideoResponse
     */
    @Transactional
    public VideoResponse publish(VideoPublish videoPublish, Long userId) {
//        User user = publishService.findUploader(oAuth2User);
        Video video = Video.fromPublish(videoPublish, userId);
        video = videoRepository.save(video);
        return VideoResponse.from(video);
    }

    /**
     * 해당 영상에 대한 유저 재생 요청
     *
     * @param videoId (Long)
     * @param userId  (Long)
     * @return PlaybackResponse
     */
    @Transactional
    public PlaybackResponse start(Long videoId, Long userId) {
        Playback playback = userPlaybackService.startUserPlayback(videoId, userId);
        return PlaybackResponse.from(playback);
    }

    /**
     * 해당 영상에 대한 유저 정지 요청
     *
     * @param videoId (Long)
     * @param userId  (Long)
     */
    @Transactional
    public PlaybackResponse stop(Long videoId, StopRequest stopRequest, Long userId) {
        Playback playback = userPlaybackService.stopUserPlayback(videoId, stopRequest, userId);
        return PlaybackResponse.from(playback);
    }
}
