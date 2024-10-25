package com.streaming.settlement.video.service;

import com.streaming.settlement.user.dto.User;
import com.streaming.settlement.user.security.CustomOAuth2User;
import com.streaming.settlement.video.dto.Video;
import com.streaming.settlement.video.dto.VideoPublish;
import com.streaming.settlement.video.repository.VideoCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VideoCommandService {

    private final VideoCommandRepository videoCommandRepository;
    private final PublishService publishService;

    /**
     * 영상 업로드
     *
     * @param videoPublish (VideoPublish)
     * @param authority    (CustomOAuth2User)
     * @return VideoResponse
     */
    @Transactional
    public Video publish(VideoPublish videoPublish, CustomOAuth2User authority) {
        User user = publishService.findUser(authority);
        System.out.println(user.getCreatedAt());
        System.out.println(user.getModifiedAt());
        Video video = Video.fromPublish(videoPublish, user);
        video = videoCommandRepository.save(video);

        return video;
    }
}
