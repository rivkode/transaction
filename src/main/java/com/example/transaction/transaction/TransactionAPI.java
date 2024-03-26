package com.example.transaction.transaction;

import com.example.transaction.transaction.dto.request.CreateTransactionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionAPI {
    private final TransactionService transactionService;
    @PostMapping
    public Transaction transaction(@RequestBody @Valid CreateTransactionRequest request) {

        return transactionService.createTransaction(request);
    }

    @GetMapping
    public List<Transaction> getTransactionAll() {
        return transactionService.getAllTransaction();
    }

    @DeleteMapping
    public void deleteTransactionAll() {
        transactionService.deleteTransactionAll();
    }
}
