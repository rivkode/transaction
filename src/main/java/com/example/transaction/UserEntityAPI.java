package com.example.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserEntityAPI {
    private final UserEntityService userEntityService;

    @PostMapping
    public String hello() {
        LocalDateTime time = LocalDateTime.now();
        return userEntityService.create("jonghun", time);
    }
}
