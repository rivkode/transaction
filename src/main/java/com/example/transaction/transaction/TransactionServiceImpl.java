package com.example.transaction.transaction;

import com.example.transaction.transaction.dto.request.CreateTransactionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction createTransaction(CreateTransactionRequest request) {
        /**
         * Todo
         * 생성시 송금인의 예산이 초과되는지 확인 로직 필요
         */
        Transaction transaction = Transaction.builder()
                .time(Instant.now())
                .amount(request.amount())
                .sendUserId(request.sendUserId())
                .receiveUserId(request.receiveUserId())
                .build();
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getAllTransaction() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction getTransactionById(String id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteTransactionById(String id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public void deleteTransactionAll() {
        transactionRepository.deleteAll();
    }
}
