package com.example.transaction.user;

import com.example.transaction.util.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserEntityAPI {
    private final UserEntityService userEntityService;
    private final MailService mailService;

    @PostMapping("/hello")
    public String hello() {
        LocalDateTime time = LocalDateTime.now();
        return userEntityService.create("jonghun", time);
    }

    @PostMapping("/email")
    public void createEmail() {
        mailService.createEmail("river@g.seoultech.ac.kr");
    }
}
