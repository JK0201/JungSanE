package com.streaming.settlement.playback.service;

import com.streaming.settlement.playback.dto.Playback;
import com.streaming.settlement.playback.repository.PlaybackRepository;
import com.streaming.settlement.user.dto.User;
import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.repository.UserRepository;
import com.streaming.settlement.user.security.CustomOAuth2User;
import com.streaming.settlement.video.dto.Video;
import com.streaming.settlement.video.entity.VideoStatus;
import com.streaming.settlement.video.exception.ResourceNotFoundException;
import com.streaming.settlement.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j(topic = "유저 영상 시청")
@Service
@RequiredArgsConstructor
public class UserPlaybackService {

    private final UserRepository userRepository;
    private final PlaybackRepository playbackRepository;
    private final VideoRepository videoRepository;

    public Playback getUserPlayback(Long videoId, CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getUsername();
        AuthProvider authProvider = oAuth2User.getAuthProvider();

        User user = userRepository.findByAuthProviderAndUsername(authProvider, username)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));
        Video video = Video.updateViewCount(
                videoRepository.findByIdAndStatus(videoId, VideoStatus.ACTIVE)
                        .orElseThrow(() -> new ResourceNotFoundException("해당 영상을 찾을 수 없습니다. : video_id " + videoId)));

        log.info("요청 영상 = video_id : {}, title : {}", video.getId(), video.getTitle());
        
        return playbackRepository.findByUserIdAndVideoId(user.getId(), videoId)
                .orElseGet(() -> Playback.from(user, video, 0L));
    }
}
