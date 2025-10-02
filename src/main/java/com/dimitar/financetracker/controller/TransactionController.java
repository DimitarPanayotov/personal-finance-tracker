package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{transactionId}/duplicate")
    public ResponseEntity<TransactionResponse> duplicateTransaction(@PathVariable Long transactionId) {
        TransactionResponse response = transactionService.duplicateTransaction(transactionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Long transactionId,
                                                                 @Valid @RequestBody UpdateTransactionRequest request) {
        request.setTransactionId(transactionId);
        TransactionResponse response = transactionService.updateTransaction(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions() {
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsInDateRange(
        @RequestParam("startDate") LocalDate startDate,
        @RequestParam("endDate") LocalDate endDate) {
        List<TransactionResponse> responses = transactionService.getTransactionsInDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByCategory(@PathVariable Long categoryId) {
        List<TransactionResponse> responses = transactionService.getTransactionsByCategory(categoryId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/amount-range")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAmountRange(
        @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
        @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount) {
        List<TransactionResponse> responses = transactionService.getTransactionsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TransactionResponse>> searchTransactions(
        @RequestParam("q") String q) {
        List<TransactionResponse> responses = transactionService.searchTransactionsByDescription(q);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(
        @RequestParam(value = "limit", required = false) Integer limit) {
        List<TransactionResponse> responses = transactionService.getRecentTransactions(limit);
        return ResponseEntity.ok(responses);
    }
}
