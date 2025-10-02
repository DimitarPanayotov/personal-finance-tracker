package com.dimitar.financetracker.dto.mapper;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.dto.response.transaction.TransactionSummaryResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionMapper {

    public Transaction toEntity(CreateTransactionRequest request, User user, Category category) {
        if (request == null) {
            return null;
        }
        return Transaction.builder()
            .user(user)
            .category(category)
            .amount(request.getAmount())
            .description(request.getDescription() != null ? request.getDescription().trim() : null)
            .transactionDate(request.getTransactionDate())
            .build();
    }

    public TransactionResponse toResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return TransactionResponse.builder()
            .id(transaction.getId())
            .userId(transaction.getUser() != null ? transaction.getUser().getId() : null)
            .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
            .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
            .amount(transaction.getAmount())
            .description(transaction.getDescription())
            .transactionDate(transaction.getTransactionDate())
            .createdAt(transaction.getCreatedAt())
            .updatedAt(transaction.getUpdatedAt())
            .build();
    }

    public TransactionSummaryResponse toSummaryResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return TransactionSummaryResponse.builder()
            .id(transaction.getId())
            .userId(transaction.getUser() != null ? transaction.getUser().getId() : null)
            .categoryId(transaction.getCategory() != null ? transaction.getCategory().getId() : null)
            .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
            .amount(transaction.getAmount())
            .description(transaction.getDescription())
            .transactionDate(transaction.getTransactionDate())
            .build();
    }

    public void updateEntity(Transaction transaction, UpdateTransactionRequest request, Category category) {
        if (transaction == null || request == null) {
            return;
        }

        if (category != null) {
            transaction.setCategory(category);
        }

        if (request.getAmount() != null) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            String trimmedDescription = request.getDescription().trim();
            transaction.setDescription(trimmedDescription.isEmpty() ? null : trimmedDescription);
        }

        if (request.getTransactionDate() != null) {
            transaction.setTransactionDate(request.getTransactionDate());
        }
    }
}