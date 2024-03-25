package com.example.transaction.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "user_entity")
public class UserEntity {

    @Id
    private String id;

    @Column(name = "name")
    private String name;

    @Builder
    public UserEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
