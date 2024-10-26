package com.streaming.settlement.playback.service;

import com.streaming.settlement.playback.dto.Playback;
import com.streaming.settlement.playback.repository.PlaybackRepository;
import com.streaming.settlement.user.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaybackCommandService {

    private final UserPlaybackService userPlaybackService;
    private final PlaybackRepository playbackRepository;

    @Transactional
    public Playback start(Long videoId, CustomOAuth2User oAuth2User) {
        Playback playback = userPlaybackService.getUserPlayback(videoId, oAuth2User);
        return playbackRepository.save(playback);
    }

    @Transactional
    public Playback stop(Long videoId, CustomOAuth2User oAuth2User) {
        return null;
    }
}
