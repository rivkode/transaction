package com.example.transaction.transaction;


import com.example.transaction.transaction.dto.request.CreateTransactionRequest;

import java.time.Instant;
import java.util.List;

public interface TransactionService {
    Transaction createTransaction(CreateTransactionRequest request);

    List<Transaction> getAllTransaction();

    Transaction getTransactionById(String id);

    void deleteTransactionById(String id);

    void deleteTransactionAll();
}
