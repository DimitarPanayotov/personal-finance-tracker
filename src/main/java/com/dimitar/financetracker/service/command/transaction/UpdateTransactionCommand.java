package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class UpdateTransactionCommand implements Command<UpdateTransactionRequest, TransactionResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse execute(UpdateTransactionRequest input) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        Transaction transaction = transactionRepository.findByIdAndUserId(input.getTransactionId(), authenticatedUserId)
            .orElseThrow(() -> new TransactionDoesNotExistException("Transaction not found or access denied!"));

        Category category = categoryRepository.findById(input.getCategoryId())
                .orElse(null);

        transactionMapper.updateEntity(transaction, input, category);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedTransaction);

    }
}
