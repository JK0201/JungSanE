package com.streaming.videoservice.repository;

import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.entity.VideoStatus;
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
