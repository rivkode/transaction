package com.example.transaction;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserEntityService {
    private final UserEntityRepository userEntityRepository;
    private final IdGenerater idGenerater;

    public String create(String name, LocalDateTime createAt) {
        String generatedName = idGenerater.generateId(name, createAt);


        UserEntity userEntity = UserEntity.builder()
                .id(generatedName)
                .name(name)
                .build();
        userEntityRepository.save(userEntity);

        return userEntity.toString();
    }
}
