package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchTransactionsByDescriptionQuery implements Query<String, List<TransactionResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionResponse> execute(String term) {
        if (term == null || term.trim().isEmpty()) {
            throw new IllegalArgumentException("q is required");
        }
        Long userId = authenticationFacade.getAuthenticatedUserId();
        List<Transaction> transactions = transactionRepository
            .findByUserIdAndDescriptionContainingIgnoreCase(userId, term.trim());
        return transactions.stream()
            .map(transactionMapper::toResponse)
            .toList();
    }
}

