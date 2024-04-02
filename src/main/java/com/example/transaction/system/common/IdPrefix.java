package com.example.transaction.system.common;

import lombok.Getter;

@Getter
public enum IdPrefix {
    USER("user"),
    TRANSACTION("transaction");

    private final String value;

    IdPrefix(String value) {
        this.value = value;
    }
}
