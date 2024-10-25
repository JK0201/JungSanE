package com.streaming.settlement.video.repository;

import com.streaming.settlement.video.dto.Video;
import com.streaming.settlement.video.entity.VideoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VideoCommandRepositoryImpl implements VideoCommandRepository {

    private final VideoJpaCommandRepository videoJpaCommandRepository;

    @Override
    public Video save(Video video) {
        return videoJpaCommandRepository.save(VideoEntity.from(video)).toModel();
    }
}
