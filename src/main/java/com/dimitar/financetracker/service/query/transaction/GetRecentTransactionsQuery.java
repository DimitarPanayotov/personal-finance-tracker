package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRecentTransactionsQuery implements Query<Integer, List<TransactionResponse>> {
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionResponse> execute(Integer limit) {
        Long userId = authenticationFacade.getAuthenticatedUserId();

        int size = (limit == null) ? DEFAULT_LIMIT : Math.max(1, Math.min(limit, MAX_LIMIT));

        Sort sort = Sort.by(
            Sort.Order.desc("transactionDate"),
            Sort.Order.desc("createdAt")
        );
        Pageable pageable = PageRequest.of(0, size, sort);

        Page<Transaction> page = transactionRepository.findByUserId(userId, pageable);

        return page.getContent().stream()
            .map(transactionMapper::toResponse)
            .toList();
    }
}

