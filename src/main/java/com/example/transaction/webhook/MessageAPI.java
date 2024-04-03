package com.example.transaction.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discord")
public class MessageAPI {
    private final MessageService messageService;

    @PostMapping("/message")
    public void sendDiscord() {
        messageService.send("hello");
    }
}
