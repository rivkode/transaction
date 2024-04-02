package com.example.transaction.user.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record CreateUserRequest(
        @NotEmpty(message = "이름은 4자 이상이어야 합니다.")
        String name
) {

}
