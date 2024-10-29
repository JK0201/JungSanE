package com.streaming.settlement.playback.service;

import com.streaming.settlement.advertisement.entity.VideoAdvertisement;
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

import java.util.List;

@Slf4j(topic = "유저 영상 시청")
@Service
@RequiredArgsConstructor
public class UserPlaybackService {

    private final UserRepository userRepository;
    private final PlaybackRepository playbackRepository;
    private final VideoRepository videoRepository;

    /**
     * 요청 유저의 해당 영상에 대한 재생 시간 조회 / 동영상 조회수 증가
     * 없을 경우 -> 재생 시간 0L return
     * 있을 경우 -> 조회된 값 return
     * FIXME 쿼리 개선 요망 (유저 / 재생 시간 Fetch 영상 따로 가져오는중)
     * FIXME 추후 MSA 생각해서 Video랑 합쳐서 Video기준으로 left join 가져오기 고려
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

        // 영상 누적 조회수 업데이트 (Dirty Checking)
        Video video = videoRepository.findByIdAndStatus(videoId, VideoStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("해당 영상을 찾을 수 없습니다. : video_id : " + videoId));

        log.info("기존 영상 조회수 = video_id : {}, total : {}", videoId, video.getAccumulatedViewCount());
        video.addAccumulatedViewCount();
        log.info("조회수 증가 = video_id : {}, total : {}", videoId, video.getAccumulatedViewCount());

        return playbackRepository.findByUserIdAndVideoId(user.getId(), videoId)
                .map(playback -> {
                    if (playback.getLastPlayPosition() >= video.getPlaybackTime()) {
                        return Playback.createUserPlayback(0L, user, video);
                    }
                    return Playback.createUserPlayback(playback.getLastPlayPosition(), user, video);
                }).orElse(Playback.createUserPlayback(0L, user, video));
    }

    /**
     * 요청 유저의 해당 영상에 대한 재생 시간 조회 / 동영상 누적 시청 길이 증가
     * 영상 광고 등록 여부 + 유저 시청에 따라 조회한 광고 조회수++
     * 재생 시간이 0초 이상이면 유저 재생 log 기록
     * FIXME 쿼리 개선 요망 (유저 / 재생 시간 Fetch 영상) + 다중 쿼리 발생 (벌크 연산 생각하기)
     * FIXME 다중 요청에 따른 제한 (어뷰징) 생각하기
     *
     * @param videoId     (Long)
     * @param stopRequest (StopRequest)
     * @param oAuth2User  (CustomOAuth2User)
     * @return Playback
     */

    public Playback stopUserPlayback(Long videoId, StopRequest stopRequest, CustomOAuth2User oAuth2User) {
        String username = oAuth2User.getUsername();
        AuthProvider authProvider = oAuth2User.getAuthProvider();

        // DB에서 유저 조회
        User user = userRepository.findByAuthProviderAndUsername(authProvider, username)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));

        // Fetch Join을 사용하여 유저 재생 시간과 해당 영상을 가져옴
        Video video = videoRepository.findByIdAndStatus(videoId, VideoStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("해당 영상을 찾을 수 없습니다. : video_id : " + videoId));

        Long previousPosition = playbackRepository.findByUserIdAndVideoId(user.getId(), videoId)
                .map(playback -> {
                    if (playback.getLastPlayPosition() >= video.getPlaybackTime()) {
                        return 0L;
                    }
                    return playback.getLastPlayPosition();
                }).orElse(0L);

        // 영상 시청 시간 계산 로직
        Long currentPosition = stopRequest.getCurrentPosition();
        Long playbackTime = video.getPlaybackTime();

        // 영상 길이보다 길게 들어올 경우를 대비
        if (currentPosition >= playbackTime) currentPosition = playbackTime;
        Long playedTime = currentPosition - previousPosition;
        // FIXME Exception 처리 고려
        if (playedTime <= 0) return null;

        // 영상 누적 시간 업데이트 (Dirty Checking)
        log.info("유저 시청 시간 = {}", playedTime);
        log.info("기존 영상 시청 시간 = video_id : {}, total : {}", videoId, video.getAccumulatedPlaybackTime());
        video.addAccumulatedPlaybackTime(playedTime);
        log.info("영상 시청 시간 증가 = video_id : {}, total : {}", videoId, video.getAccumulatedPlaybackTime());

        // 유저 재생시간 log 생성
        Playback playback = Playback.createUserPlayback(currentPosition, user, video);
        playback.updateVideoPlayedTime(playedTime);

        // 광고가 있을경우 본 광고 횟수 계산
        // FIXME Lazy로 광고 추가 쿼리로 따로 가져오는 중
        List<VideoAdvertisement> videoAdvertisementList = video.getVideoAdvertisementList();
        if (!videoAdvertisementList.isEmpty()) {
            for (VideoAdvertisement videoAdvertisement : videoAdvertisementList) {
                if (videoAdvertisement.getPlaybackTime() > previousPosition
                        && videoAdvertisement.getPlaybackTime() <= currentPosition) {
                    log.info("광고 조회 = 광고 시청 지점 : {}, 유저 영상 정지 시점 : {}", videoAdvertisement.getPlaybackTime(), currentPosition);
                    log.info("기존 광고 조회수 = videoAdvertisement_id : {}, total : {}", videoAdvertisement.getId(), videoAdvertisement.getAccumulatedViewCount());
                    videoAdvertisement.addAccumulatedViewCount();
                    playback.addAdvertisementViewCount();
                    log.info("광고 조회수 증가= videoAdvertisement_id : {}, total : {}", videoAdvertisement.getId(), videoAdvertisement.getAccumulatedViewCount());
                }
            }
        }

        return playbackRepository.save(playback);
    }
}
