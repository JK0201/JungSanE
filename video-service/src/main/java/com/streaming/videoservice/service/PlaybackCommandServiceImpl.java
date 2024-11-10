package com.streaming.videoservice.service;

import com.streaming.common.exception.ResourceNotFoundException;
import com.streaming.videoservice.config.redis.RedisService;
import com.streaming.videoservice.config.redis.RedissonLockFacade;
import com.streaming.videoservice.controller.port.PlaybackCommandService;
import com.streaming.videoservice.dto.request.PlaybackLogEvent;
import com.streaming.videoservice.dto.request.VideoStop;
import com.streaming.videoservice.dto.response.PlaybackResponse;
import com.streaming.videoservice.entity.playback.Playback;
import com.streaming.videoservice.entity.playback.PlaybackStatus;
import com.streaming.videoservice.entity.video.Video;
import com.streaming.videoservice.entity.video.VideoAdvertisement;
import com.streaming.videoservice.entity.video.VideoStatus;
import com.streaming.videoservice.service.port.PlaybackRepository;
import com.streaming.videoservice.service.port.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaybackCommandServiceImpl implements PlaybackCommandService {

    private static final String ABUSE_PREFIX = "abuse:video:";
    private static final String LOCK_PREFIX = "lock:video:";
    private static final long ABUSE_PREVENTION_SECONDS = 30L;

    private final PlaybackRepository playbackRepository;
    private final VideoRepository videoRepository;
    private final RedissonLockFacade redissonLockFacade;
    private final RedisService redisService;

    /**
     * 영상 재생 시작 처리
     * - 동시성 제어를 위해 Redisson 분산락 사용
     * - 재생 기록이 없는 경우 새로운 재생 기록 생성
     * - 해당 영상 업로더가 아닌 시청자의 경우, 어뷰징 체크 후 조회수 증가
     *
     * @param videoId 시청 videoId (Long)
     * @param userId  영상 시작 요청 userId (Long)
     * @return PlaybackResponse
     */
    public PlaybackResponse start(Long videoId, Long userId) {
        return redissonLockFacade.executeWithLock(LOCK_PREFIX + videoId, () -> {
            // 해당 영상 유저 재생 기록 DB에 요청 -> 없을 시, 새로운 재생 기록 생성 및 저장
            Playback playback = playbackRepository.findByUserIdAndVideoIdOnStart(userId, videoId)
                    .orElseGet(() -> {
                        Video video = videoRepository.findByIdAndStatus(videoId, VideoStatus.ACTIVE)
                                .orElseThrow(() -> new ResourceNotFoundException("해당 영상을 찾을 수 없습니다."));
                        return playbackRepository.save(Playback.createPlayback(userId, video));
                    });

            // 해당 영상 업로더가 아닐 경우 블록 내부 진행
            Video video = playback.getVideo();
            if (!video.getUploaderId().equals(userId)) {
                // 30초 어뷰징 확인 (Redis)
                String abuseKey = ABUSE_PREFIX + videoId + ":" + userId;
                Boolean isFirstView = redisService.checkFirstAccess(
                        abuseKey,
                        "1",
                        ABUSE_PREVENTION_SECONDS,
                        TimeUnit.SECONDS);

                // 첫 시청일 경우, 영상 누적 조회수 1 증가
                // 시청 유저의 시청 상태 변경
                if (Boolean.TRUE.equals(isFirstView)) {
                    playback.updateStatus(PlaybackStatus.PROGRESS);
                    video.incrementAccumulatedViewCount();
                    log.info("=== video_id : {} ===", videoId);
                    log.info("조회수 증가 : {}", video.getAccumulatedViewCount());
                } else {
                    log.warn("Abuse has been detected");
                    // 어뷰징일 경우 COMPLETE로 유지 (조회수에 포함x, 시청 기록은 유지하기 위함)
                    playback.updateStatus(PlaybackStatus.COMPLETE);
                }
            }

            return PlaybackResponse.from(playback);
        });
    }

    /**
     * 동영상 재생 중지 처리
     * - 동시성 제어를 위해 Redisson 분산락 사용
     * - 마지막 재생 위치 업데이트
     * - 해당 영상 업로더가 아닌 시청자의 경우, 광고 조회수 및 시청 시간 기록
     * - adjustment-service에 PlaybackLog(로그 데이터) 생성 및
     * FIXME adjustment-service 요청 마무리 (Kafka예정)
     *
     * @param videoId   시청 종료 videoId (Long)
     * @param videoStop 종료 시점 재생 위치 (StopRequest)
     * @param userId    영상 종료 요청 userId (Long)
     */
    public void stop(Long videoId, VideoStop videoStop, Long userId) {
        redissonLockFacade.executeWithLock(LOCK_PREFIX + videoId, () -> {
            // 해당 영상 유저 재생 기록 DB에 요청
            Playback playback = playbackRepository.findByUserIdAndVideoIdOnStop(userId, videoId)
                    .orElseThrow(() -> new ResourceNotFoundException("최근 재생 기록을 찾을 수 없습니다."));

            // 유저의 영상 재생 시간 계산
            Video video = playback.getVideo();
            Long currentPosition = Math.min(videoStop.getCurrentPosition(), video.getPlaybackTime()); // 영상의 길이 보다 길게 요청이 들어올 경우 대비
            Long playedTime = currentPosition - playback.getLastPlayPosition();

            // 영상 시간에 변동이 없다면 종료
            if (playedTime <= 0) return null;

            // 해당 영상 업로더가 아닐 경우 블록 내부 진행
            if (!video.getUploaderId().equals(userId)) {
                // 광고 조회수 계산
                Long advertisementViewCount = countAdvertisementViews(video, playback.getLastPlayPosition(), currentPosition);
                // adjustment-service에 보낼 PlaybackLogEvent DTO 객체 생성
                PlaybackLogEvent event = PlaybackLogEvent.from(
                        video,
                        playedTime,
                        advertisementViewCount,
                        playback.getStatus());

                log.info("=== video_id : {} ===", videoId);
                log.info("광고 조회수 : {}", event.getAdvertisementViewCount());
                log.info("영상 시청 시간 : {}", event.getVideoPlayedTime());
            }

            // 영상을 다 봤다면 유저 시청 위치를 0L로 초기화
            if (currentPosition.equals(video.getPlaybackTime())) playback.updateLastPosition(0L);
            else playback.updateLastPosition(currentPosition);

            // 유저 시청 상태 변경
            playback.updateStatus(PlaybackStatus.COMPLETE);

            return null;
        });
    }

    /**
     * 특정 구간(5분 간격 - 프로젝트 요구 사항) 동안 시청된 광고 수 계산
     * - 마지막 재생 위치와, 현재 위치 사이에 있는 광고들의 조회수 증가
     *
     * @param video            영상 정보 (Video)
     * @param lastPlayPosition start 이전 시청 위치 (Long)
     * @param currentPosition  종료 시점 재생 위치 (Long)
     * @return 시청된 광고수 (long)
     */
    private Long countAdvertisementViews(Video video, Long lastPlayPosition, Long currentPosition) {
        List<VideoAdvertisement> viewedAdvertisements = video.getVideoAdvertisementList().stream()
                .filter(advertisement -> advertisement.getPlaybackTime() > lastPlayPosition
                        && advertisement.getPlaybackTime() <= currentPosition)
                .toList();

        viewedAdvertisements.forEach(VideoAdvertisement::addAccumulatedViewCount);
        return (long) viewedAdvertisements.size();
    }
}
