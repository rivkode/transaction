package com.example.transaction.util;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

@Valid
public record EmailRequest (
        @NotEmpty(message = "이메일을 입력해주세요.")
        @Email
        String email,

        @NotEmpty(message = "인증 번호를 입력해주세요.")
        String authNumber
){
}
