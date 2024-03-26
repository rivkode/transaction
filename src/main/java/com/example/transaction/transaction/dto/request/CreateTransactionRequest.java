package com.example.transaction.transaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateTransactionRequest(
        @NotBlank(message = "송금 금액은 1원 이상이어야 합니다.")
        Long amount,

        @NotEmpty(message = "송금인의 Id는 필수 입력값입니다.")
        String sendUserId,

        @NotEmpty(message = "수취인의 Id는 필수 입력값입니다.")
        String receiveUserId
) {
}
