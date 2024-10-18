package com.streaming.settlement.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserCommandController {

    @PostMapping("/v1/test")
    public String test() {
        return "Hello World";
    }
}
