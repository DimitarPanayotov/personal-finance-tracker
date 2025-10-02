package com.dimitar.financetracker.service.command.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.request.transaction.UpdateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Category;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.category.CategoryDoesNotExistException;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.model.CategoryType;
import com.dimitar.financetracker.repository.CategoryRepository;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateTransactionCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TransactionMapper transactionMapper;

    private UpdateTransactionCommand command;

    @BeforeEach
    void setUp() {
        command = new UpdateTransactionCommand(authenticationFacade, categoryRepository, transactionRepository, transactionMapper);
    }

    @Test
    void execute_updatesTransaction_whenFound() {
        Long userId = 5L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Long txId = 100L;
        Transaction existing = Transaction.builder().id(txId).amount(new BigDecimal("1.00")).description("old").transactionDate(LocalDate.of(2025,1,1)).build();
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.of(existing));

        Long categoryId = 10L;
        Category category = Category.builder().id(categoryId).name("Food").type(CategoryType.EXPENSE).build();
        when(categoryRepository.findByIdAndUserId(categoryId, userId)).thenReturn(Optional.of(category));

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .transactionId(txId)
                .categoryId(categoryId)
                .amount(new BigDecimal("12.34"))
                .description("  updated  ")
                .transactionDate(LocalDate.of(2025,2,2))
                .build();

        when(transactionRepository.save(existing)).thenReturn(existing);
        TransactionResponse expected = TransactionResponse.builder().id(txId).categoryId(categoryId).amount(new BigDecimal("12.34")).description("updated").transactionDate(LocalDate.of(2025,2,2)).build();
        when(transactionMapper.toResponse(existing)).thenReturn(expected);

        TransactionResponse result = command.execute(request);

        verify(transactionMapper).updateEntity(existing, request, category);
        verify(transactionRepository).save(existing);
        assertEquals(expected, result);
    }

    @Test
    void execute_throwsWhenTransactionNotFound() {
        Long userId = 5L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(999L, userId)).thenReturn(Optional.empty());

        UpdateTransactionRequest request = UpdateTransactionRequest.builder().transactionId(999L).build();

        assertThrows(TransactionDoesNotExistException.class, () -> command.execute(request));
        verifyNoInteractions(categoryRepository, transactionMapper);
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void execute_throwsWhenCategoryNotFoundOrNotOwned() {
        Long userId = 5L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Long txId = 100L;
        Transaction existing = Transaction.builder().id(txId).build();
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.of(existing));

        when(categoryRepository.findByIdAndUserId(555L, userId)).thenReturn(Optional.empty());

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .transactionId(txId)
                .categoryId(555L)
                .amount(new BigDecimal("20.00"))
                .build();

        assertThrows(CategoryDoesNotExistException.class, () -> command.execute(request));
        verify(transactionRepository, never()).save(any());
        verify(transactionMapper, never()).updateEntity(any(), any(), any());
    }

    @Test
    void execute_updatesWithoutChangingCategory_whenCategoryIdNull() {
        Long userId = 5L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Long txId = 100L;
        Transaction existing = Transaction.builder().id(txId).amount(new BigDecimal("1.00")).description("old").transactionDate(LocalDate.of(2025,1,1)).build();
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.of(existing));

        UpdateTransactionRequest request = UpdateTransactionRequest.builder()
                .transactionId(txId)
                .categoryId(null)
                .amount(new BigDecimal("2.50"))
                .description("new desc")
                .transactionDate(LocalDate.of(2025,1,2))
                .build();

        when(transactionRepository.save(existing)).thenReturn(existing);
        TransactionResponse expected = TransactionResponse.builder().id(txId).amount(new BigDecimal("2.50")).description("new desc").transactionDate(LocalDate.of(2025,1,2)).build();
        when(transactionMapper.toResponse(existing)).thenReturn(expected);

        TransactionResponse result = command.execute(request);

        verify(categoryRepository, never()).findByIdAndUserId(anyLong(), anyLong());
        verify(transactionMapper).updateEntity(existing, request, null);
        verify(transactionRepository).save(existing);
        assertEquals(expected, result);
    }
}
