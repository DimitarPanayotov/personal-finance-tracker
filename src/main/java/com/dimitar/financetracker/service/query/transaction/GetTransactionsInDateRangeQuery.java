package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetTransactionsInDateRangeQuery implements Query<GetTransactionsInDateRangeQuery.DateRange,
    List<TransactionResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public record DateRange(LocalDate startDate, LocalDate endDate) {
    }

    @Override
    public List<TransactionResponse> execute(DateRange input) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        LocalDate start = input.startDate();
        LocalDate end = input.endDate();

        if (start == null || end == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        if (start.isAfter(end)) {
            LocalDate tmp = start;
            start = end;
            end = tmp;
        }

        List<Transaction> transactions = transactionRepository
            .findByUserIdAndTransactionDateBetween(authenticatedUserId, start, end);

        return transactions.stream()
            .map(transactionMapper::toResponse)
            .toList();
    }
}

