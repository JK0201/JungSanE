package com.streaming.settlement.playback.service;

import com.streaming.settlement.playback.dto.PlaybackResponse;
import com.streaming.settlement.playback.dto.StopRequest;
import com.streaming.settlement.playback.entity.Playback;
import com.streaming.settlement.user.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaybackCommandService {

    private final UserPlaybackService userPlaybackService;

    /**
     * 해당 영상에 대한 유저 재생 요청
     *
     * @param videoId    (Long)
     * @param oAuth2User (CustomOAuth2User)
     * @return PlaybackResponse
     */
    @Transactional
    public PlaybackResponse start(Long videoId, CustomOAuth2User oAuth2User) {
        Playback playback = userPlaybackService.startUserPlayback(videoId, oAuth2User);
        return PlaybackResponse.from(playback);
    }

    /**
     * 해당 영상에 대한 유저 정지 요청
     *
     * @param videoId    (Long)
     * @param oAuth2User (CustomOAuth2User)
     */
    @Transactional
    public PlaybackResponse stop(Long videoId, StopRequest stopRequest, CustomOAuth2User oAuth2User) {
        Playback playback = userPlaybackService.stopUserPlayback(videoId, stopRequest, oAuth2User);
        return PlaybackResponse.from(playback);
    }
}
