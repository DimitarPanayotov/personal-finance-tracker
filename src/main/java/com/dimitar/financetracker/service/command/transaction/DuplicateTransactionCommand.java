package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
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
public class DuplicateTransactionCommand implements Command<Long, TransactionResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse execute(Long transactionId) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        Transaction source = transactionRepository.findByIdAndUserId(transactionId, authenticatedUserId)
            .orElseThrow(() -> new TransactionDoesNotExistException("Transaction not found or access denied!"));

        User user = source.getUser();
        Category category = source.getCategory();

        CreateTransactionRequest request = CreateTransactionRequest.builder()
            .categoryId(category != null ? category.getId() : null)
            .amount(source.getAmount())
            .description(source.getDescription())
            .transactionDate(source.getTransactionDate())
            .build();

        Transaction duplicate = transactionMapper.toEntity(request, user, category);
        Transaction saved = transactionRepository.save(duplicate);
        return transactionMapper.toResponse(saved);
    }
}

