package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.exception.transaction.TransactionDoesNotExistException;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionByIdQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetTransactionByIdQuery query;

    @BeforeEach
    void setUp() { query = new GetTransactionByIdQuery(authenticationFacade, transactionRepository, transactionMapper); }

    @Test
    void execute_returnsMapped_whenFound() {
        Long userId = 2L; Long txId = 10L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        Transaction tx = Transaction.builder().id(txId).build();
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.of(tx));
        TransactionResponse expected = TransactionResponse.builder().id(txId).build();
        when(transactionMapper.toResponse(tx)).thenReturn(expected);

        TransactionResponse result = query.execute(txId);
        assertEquals(expected, result);
        verify(transactionRepository).findByIdAndUserId(txId, userId);
        verify(transactionMapper).toResponse(tx);
    }

    @Test
    void execute_throwsWhenNotFound() {
        Long userId = 2L; Long txId = 999L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.empty());
        assertThrows(TransactionDoesNotExistException.class, () -> query.execute(txId));
        verifyNoInteractions(transactionMapper);
    }
}

