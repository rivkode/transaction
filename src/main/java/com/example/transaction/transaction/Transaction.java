package com.example.transaction.transaction;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "transaction_entity")
@Getter
public class Transaction {
    @Id
    private String id;

    private Instant time;

    private Long amount;

    private String sendUserId;

    private String receiveUserId;

    @Builder
    public Transaction(Instant time, Long amount, String sendUserId, String receiveUserId) {
        this.time = time;
        this.amount = amount;
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
    }
}
