package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuplicateTransactionCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;

    private DuplicateTransactionCommand command;

    @BeforeEach
    void setUp() {
        command = new DuplicateTransactionCommand(authenticationFacade, transactionRepository, transactionMapper);
    }

    @Test
    void execute_duplicatesTransaction_whenFound() {
        Long userId = 8L;
        Long txId = 55L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        User user = User.builder().id(userId).username("sam").build();
        Category category = Category.builder().id(10L).name("Groceries").type(CategoryType.EXPENSE).build();
        Transaction source = Transaction.builder()
                .id(txId)
                .user(user)
                .category(category)
                .amount(new BigDecimal("19.99"))
                .description("milk and bread")
                .transactionDate(LocalDate.of(2025, 3, 3))
                .build();
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.of(source));

        // The command builds a CreateTransactionRequest internally; we just ensure mapper.toEntity is called with an entity
        Transaction duplicated = Transaction.builder()
                .user(user)
                .category(category)
                .amount(source.getAmount())
                .description(source.getDescription())
                .transactionDate(source.getTransactionDate())
                .build();
        when(transactionMapper.toEntity(any(), eq(user), eq(category))).thenReturn(duplicated);
        when(transactionRepository.save(duplicated)).thenReturn(duplicated);

        TransactionResponse expected = TransactionResponse.builder().id(777L).userId(userId).categoryId(10L)
                .amount(new BigDecimal("19.99")).description("milk and bread").transactionDate(LocalDate.of(2025,3,3)).build();
        when(transactionMapper.toResponse(duplicated)).thenReturn(expected);

        TransactionResponse result = command.execute(txId);

        assertEquals(expected, result);
        verify(transactionRepository).findByIdAndUserId(txId, userId);
        verify(transactionMapper).toEntity(any(), eq(user), eq(category));
        verify(transactionRepository).save(duplicated);
        verify(transactionMapper).toResponse(duplicated);
    }

    @Test
    void execute_throwsWhenSourceNotFound() {
        Long userId = 8L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(404L, userId)).thenReturn(Optional.empty());

        assertThrows(TransactionDoesNotExistException.class, () -> command.execute(404L));
        verifyNoInteractions(transactionMapper);
        verify(transactionRepository, never()).save(any());
    }
}

