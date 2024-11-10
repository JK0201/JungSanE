package com.streaming.videoservice.repository.video;

import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.entity.VideoStatus;
import com.streaming.videoservice.service.port.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VideoRepositoryImpl implements VideoRepository {

    private final VideoJpaRepository videoJpaRepository;

    // READ
    @Override
    public Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus) {
        return videoJpaRepository.findByIdAndStatus(videoId, videoStatus);
    }

    // CUD
    @Override
    public void save(Video video) {
        videoJpaRepository.save(video);
    }


}
