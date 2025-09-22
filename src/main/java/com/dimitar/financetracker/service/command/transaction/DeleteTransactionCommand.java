package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class DeleteTransactionCommand implements Command<Long, Void> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;

    @Override
    public Void execute(Long transactionId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, authenticatedUserId)
            .orElseThrow(() -> new TransactionDoesNotExistException("Transaction not found or access denied!"));

        transactionRepository.delete(transaction);
        return null;
    }
}
