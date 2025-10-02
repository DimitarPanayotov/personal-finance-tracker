package com.dimitar.financetracker.service.command.transaction;

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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteTransactionCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private TransactionRepository transactionRepository;

    private DeleteTransactionCommand command;

    @BeforeEach
    void setUp() {
        command = new DeleteTransactionCommand(authenticationFacade, transactionRepository);
    }

    @Test
    void execute_deletesTransaction_whenFound() {
        Long userId = 9L;
        Long txId = 77L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction tx = Transaction.builder().id(txId).build();
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.of(tx));

        command.execute(txId);

        verify(transactionRepository).delete(tx);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void execute_throwsWhenTransactionNotFound() {
        Long userId = 9L;
        Long txId = 999L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        when(transactionRepository.findByIdAndUserId(txId, userId)).thenReturn(Optional.empty());

        assertThrows(TransactionDoesNotExistException.class, () -> command.execute(txId));
        verify(transactionRepository, never()).delete(any());
    }
}

