package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.entity.VideoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepository {

    private final VideoJpaRepository videoJpaRepository;

    @Override
    public Video save(Video video) {
        return videoJpaRepository.save(video);
    }

    @Override
    public Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus) {
        return videoJpaRepository.findByIdAndStatus(videoId, videoStatus);
    }
}
