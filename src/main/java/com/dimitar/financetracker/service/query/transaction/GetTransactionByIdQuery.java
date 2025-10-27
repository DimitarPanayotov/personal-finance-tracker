package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetTransactionByIdQuery implements Query<Long, TransactionResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse execute(Long transactionId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, authenticatedUserId)
            .orElseThrow(() -> new TransactionDoesNotExistException("Transaction not found or access denied!"));
        return transactionMapper.toResponse(transaction);
    }
}
