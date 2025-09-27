package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetTransactionsByAmountRangeQuery implements Query<GetTransactionsByAmountRangeQuery.AmountRange,
    List<TransactionResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public record AmountRange(BigDecimal minAmount, BigDecimal maxAmount) { }

    @Override
    public List<TransactionResponse> execute(AmountRange input) {
        Long userId = authenticationFacade.getAuthenticatedUserId();
        BigDecimal min = input != null ? input.minAmount() : null;
        BigDecimal max = input != null ? input.maxAmount() : null;

        if (min == null && max == null) {
            throw new IllegalArgumentException("minAmount or maxAmount is required");
        }

        List<Transaction> transactions;
        if (min != null && max != null) {
            if (min.compareTo(max) > 0) {
                BigDecimal tmp = min;
                min = max;
                max = tmp;
            }
            transactions = transactionRepository.findByUserIdAndAmountBetween(userId, min, max);
        } else if (min != null) {
            transactions = transactionRepository.findByUserIdAndAmountGreaterThanEqual(userId, min);
        } else {
            transactions = transactionRepository.findByUserIdAndAmountLessThanEqual(userId, max);
        }

        return transactions.stream()
            .map(transactionMapper::toResponse)
            .toList();
    }
}

