package com.streaming.settlement.playback.service;

import com.streaming.settlement.playback.dto.StopRequest;
import com.streaming.settlement.playback.entity.Playback;
import com.streaming.settlement.playback.repository.PlaybackRepository;
import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.user.repository.UserRepository;
import com.streaming.settlement.user.security.CustomOAuth2User;
import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.entity.VideoStatus;
import com.streaming.settlement.video.exception.ResourceNotFoundException;
import com.streaming.settlement.video.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j(topic = "유저 영상 시청")
@Service
@RequiredArgsConstructor
public class UserPlaybackService {

    private final UserRepository userRepository;
    private final PlaybackRepository playbackRepository;
    private final VideoRepository videoRepository;

    /**
     * 요청 유저의 해당 영상에 대한 재생 시간 조회 / 동영상 조회수++
     * 없을 경우 -> 초기화 및 재생 시간 insert
     * 있을 경우 -> 조회된 값 return
     * FIXME 쿼리 개선 요망
     *
     * @param videoId    (Long)
     * @param oAuth2User (CustomOAuth2User)
     * @return Playback
     */
    public Playback startUserPlayback(Long videoId, CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getUsername();
        AuthProvider authProvider = oAuth2User.getAuthProvider();

        // DB에서 유저 조회
        User user = userRepository.findByAuthProviderAndUsername(authProvider, username)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));

        // Fetch Join을 사용하여 유저 재생 시간과 해당 영상을 가져옴
        Optional<Playback> existPlayback = playbackRepository.findByUserIdAndVideoIdFetchVideo(user.getId(), videoId);
        if (existPlayback.isPresent()) {
            Playback playback = existPlayback.get();
            playback.updateStartPosition();
            playback.getVideo().addAccumulatedViewCount();
            return playback;
        }

        // 없을 경우 초기화 및 재생 시간 저장
        Video video = videoRepository.findByIdAndStatus(videoId, VideoStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("해당 영상을 찾을 수 없습니다. : video_id : " + videoId));
        Playback playback = Playback.createUserPlayback(user, video, 0L);
        return playbackRepository.save(playback);
    }

    public Playback stopUserPlayback(Long videoId, StopRequest stopRequest, CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getUsername();
        AuthProvider authProvider = oAuth2User.getAuthProvider();

        // DB에서 유저 조회
        User user = userRepository.findByAuthProviderAndUsername(authProvider, username)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));

        // Fetch Join을 사용하여 유저 재생 시간과 해당 영상을 가져옴
        Playback playback = playbackRepository.findByUserIdAndVideoIdFetchVideo(user.getId(), videoId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 재생 기록을 찾을 수 없습니다. : " +
                        "video_id : " + videoId + " / username : " + username));

        // 영상 시청 시간 계산 로직
        Long currentPosition = stopRequest.getCurrentPosition();
        Long previousPosition = playback.getLastPlayTime();
        Long playbackTime = playback.getVideo().getPlaybackTime();

        // 영상 길이보다 길게 들어올 경우를 대비
        if (currentPosition >= playbackTime) currentPosition = playbackTime;

        // 영상 누적 시간, 유저 재생 시간을 업데이트 (Dirty Checking)
        Long playedTime = currentPosition - previousPosition;
        if (playedTime > 0) {
            playback.getVideo().addAccumulatedPlaybackTime(playedTime);
            playback.update(currentPosition);
        }

        return playback;
    }
}
