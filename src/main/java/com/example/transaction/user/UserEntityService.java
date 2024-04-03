package com.example.transaction.user;

import com.example.transaction.system.common.IdGen;
import com.example.transaction.system.common.IdGenerater;
import com.example.transaction.system.common.IdPrefix;
import com.example.transaction.user.dto.request.CreateUserRequest;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserEntityService {
    private final UserEntityRepository userEntityRepository;
    private final IdGenerater idGenerater;
    private final IdGen idGen;

    public String create(CreateUserRequest request) {
        Instant time = Instant.now();
        IdPrefix idPrefix = IdPrefix.USER;
        String generatedId = idGen.generateId(request.name(), time, idPrefix);
        log.info("name : " + request.name());
        log.info("generatedId : " + generatedId);


        UserEntity userEntity = UserEntity.builder()
                .id(generatedId)
                .name(request.name())
                .build();
        userEntityRepository.save(userEntity);
        userEntityRepository.flush();

        return userEntity.toString();
    }
}
