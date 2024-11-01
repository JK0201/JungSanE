package com.streaming.settlement.video.controller;

import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.repository.VideoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlbbanoController {

    private final VideoJpaRepository videoJpaRepository;

    @GetMapping("/api/test123")
    public List<Video> test1() {
        return videoJpaRepository.findAll();
    }
}