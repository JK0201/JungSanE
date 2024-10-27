package com.streaming.settlement.video.service;

import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.user.security.CustomOAuth2User;
import com.streaming.settlement.video.dto.VideoPublish;
import com.streaming.settlement.video.dto.VideoResponse;
import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "유저 재생시간")
@Service
@RequiredArgsConstructor
public class VideoCommandService {

    private final VideoRepository videoRepository;
    private final PublishService publishService;

    /**
     * 영상 업로드
     *
     * @param videoPublish (VideoPublish)
     * @param oAuth2User   (CustomOAuth2User)
     * @return VideoResponse
     */
    @Transactional
    public VideoResponse publish(VideoPublish videoPublish, CustomOAuth2User oAuth2User) {
        User user = publishService.findUploader(oAuth2User);
        Video video = Video.fromPublish(videoPublish, user);
        video = videoRepository.save(video);

        return VideoResponse.from(video);
    }
}
