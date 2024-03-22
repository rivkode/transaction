package com.example.transaction;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class IdGenerater {
    Integer x;
    Integer y;
    Integer z;

    public String generateId(String s, LocalDateTime createAt) {
        return "";
    }

    private String parseTime(LocalDateTime createAt) {
        return "";
    }


}
