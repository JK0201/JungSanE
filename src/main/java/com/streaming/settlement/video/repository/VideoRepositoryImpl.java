package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.dto.Video;
import com.streaming.settlement.video.entity.VideoEntity;
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
        return videoJpaRepository.save(VideoEntity.from(video)).toModel();
    }

    @Override
    public Optional<Video> findByIdAndStatus(Long videoId, VideoStatus videoStatus) {
        return videoJpaRepository.findByIdAndStatus(videoId, videoStatus).map(VideoEntity::toModel);
    }
}
