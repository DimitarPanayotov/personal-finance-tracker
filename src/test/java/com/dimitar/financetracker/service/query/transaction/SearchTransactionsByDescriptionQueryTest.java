package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchTransactionsByDescriptionQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private SearchTransactionsByDescriptionQuery query;

    @BeforeEach
    void setUp() {
        query = new SearchTransactionsByDescriptionQuery(authenticationFacade, transactionRepository, transactionMapper);
    }

    @Test
    void execute_trimsAndQueries_whenValidTerm() {
        Long userId = 6L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction t = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserIdAndDescriptionContainingIgnoreCase(eq(userId), anyString()))
                .thenReturn(List.of(t));
        when(transactionMapper.toResponse(t)).thenReturn(TransactionResponse.builder().id(1L).build());

        List<TransactionResponse> result = query.execute("  milk  ");
        assertEquals(1, result.size());

        ArgumentCaptor<String> termCap = ArgumentCaptor.forClass(String.class);
        verify(transactionRepository).findByUserIdAndDescriptionContainingIgnoreCase(eq(userId), termCap.capture());
        assertEquals("milk", termCap.getValue());
    }

    @Test
    void execute_throwsWhenTermBlankOrNull() {
        assertThrows(IllegalArgumentException.class, () -> query.execute("   "));
        assertThrows(IllegalArgumentException.class, () -> query.execute(null));
    }
}
