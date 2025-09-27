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
public class GetTransactionsByCategoryQuery implements Query<Long, List<TransactionResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionResponse> execute(Long categoryId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        List<Transaction> transactions = transactionRepository
            .findByUserIdAndCategoryId(authenticatedUserId, categoryId);
        return transactions.stream()
            .map(transactionMapper::toResponse)
            .toList();
    }
}
