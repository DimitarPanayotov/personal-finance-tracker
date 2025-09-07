package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.command.Command;
import com.dimitar.financetracker.service.command.transaction.input.CreateTransactionCommandInput;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class CreateTransactionCommand implements Command<CreateTransactionCommandInput, TransactionResponse> {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse execute(CreateTransactionCommandInput input) {
        User user = userRepository.findById(input.userId())
            .orElseThrow(() -> new UserDoesNotExistException("User with this id does not exist: " + input.userId()));

        Category category = categoryRepository.findById(input.request().getCategoryId())
            .orElseThrow(() -> new CategoryDoesNotExistException("Category with this id does not exist: " + input.request().getCategoryId()));

        Transaction transaction = transactionMapper.toEntity(input.request(), user, category);

        Transaction savedTransaction = transactionRepository.save(transaction);
        return transactionMapper.toResponse(savedTransaction);
    }
}
