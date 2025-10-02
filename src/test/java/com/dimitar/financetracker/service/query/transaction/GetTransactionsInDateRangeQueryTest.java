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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetTransactionsInDateRangeQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetTransactionsInDateRangeQuery query;

    @BeforeEach
    void setUp() { query = new GetTransactionsInDateRangeQuery(authenticationFacade, transactionRepository, transactionMapper); }

    @Test
    void execute_returnsMapped_andSwapsDatesWhenStartAfterEnd() {
        Long userId = 10L; when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);
        LocalDate start = LocalDate.of(2025, 3, 10);
        LocalDate end = LocalDate.of(2025, 3, 1); // start > end

        Transaction t = Transaction.builder().id(1L).build();
        when(transactionRepository.findByUserIdAndTransactionDateBetween(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(t));
        when(transactionMapper.toResponse(t)).thenReturn(TransactionResponse.builder().id(1L).build());

        List<TransactionResponse> result = query.execute(new GetTransactionsInDateRangeQuery.DateRange(start, end));
        assertEquals(1, result.size());

        ArgumentCaptor<LocalDate> startCap = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endCap = ArgumentCaptor.forClass(LocalDate.class);
        verify(transactionRepository).findByUserIdAndTransactionDateBetween(eq(userId), startCap.capture(), endCap.capture());
        assertEquals(LocalDate.of(2025, 3, 1), startCap.getValue());
        assertEquals(LocalDate.of(2025, 3, 10), endCap.getValue());
    }

    @Test
    void execute_throwsWhenDatesMissing() {
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(1L);
        assertThrows(IllegalArgumentException.class, () -> query.execute(new GetTransactionsInDateRangeQuery.DateRange(null, LocalDate.now())));
        assertThrows(IllegalArgumentException.class, () -> query.execute(new GetTransactionsInDateRangeQuery.DateRange(LocalDate.now(), null)));
    }
}

