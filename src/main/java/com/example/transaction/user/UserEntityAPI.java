package com.example.transaction.user;

import com.example.transaction.user.dto.request.CreateUserRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserEntityAPI {
    private final UserEntityService userEntityService;

    @PostMapping("/hello")
    public String createTransaction(@RequestBody @Valid CreateUserRequest request) {
        return userEntityService.create(request);
    }
}
