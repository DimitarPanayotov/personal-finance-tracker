package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.request.PageRequest;
import com.dimitar.financetracker.dto.response.PagedResponse;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllTransactionsQueryTest {

    @Mock private AuthenticationFacade authenticationFacade;
    @Mock private TransactionRepository transactionRepository;
    @Mock private TransactionMapper transactionMapper;

    private GetAllTransactionsQuery query;

    @BeforeEach
    void setUp() {
        query = new GetAllTransactionsQuery(authenticationFacade, transactionRepository, transactionMapper);
    }

    @Test
    void execute_returnsMappedTransactionsForAuthenticatedUser() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction t1 = Transaction.builder().id(1L).build();
        Transaction t2 = Transaction.builder().id(2L).build();

        PageRequest pageRequest = PageRequest.builder()
                .page(0)
                .size(20)
                .sortBy("transactionDate")
                .sortDirection("DESC")
                .build();

        // Create a Spring Data Page object
        Page<Transaction> page = new PageImpl<>(List.of(t1, t2), pageRequest.toPageable(), 2);
        when(transactionRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        TransactionResponse r1 = TransactionResponse.builder().id(1L).build();
        TransactionResponse r2 = TransactionResponse.builder().id(2L).build();
        when(transactionMapper.toResponse(t1)).thenReturn(r1);
        when(transactionMapper.toResponse(t2)).thenReturn(r2);

        PagedResponse<TransactionResponse> result = query.execute(pageRequest);

        assertEquals(List.of(r1, r2), result.getContent());
        assertEquals(0, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(true, result.isFirst());
        assertEquals(true, result.isLast());
        assertEquals(false, result.isEmpty());

        verify(transactionRepository).findByUserId(eq(userId), any(Pageable.class));
        verify(transactionMapper).toResponse(t1);
        verify(transactionMapper).toResponse(t2);
        verifyNoMoreInteractions(transactionRepository, transactionMapper);
    }

    @Test
    void execute_handlesNullPageRequest_usesDefaults() {
        Long userId = 1L;
        when(authenticationFacade.getAuthenticatedUserId()).thenReturn(userId);

        Transaction t1 = Transaction.builder().id(1L).build();

        // Default page request will be created internally
        Page<Transaction> page = new PageImpl<>(List.of(t1), org.springframework.data.domain.PageRequest.of(0, 20), 1);
        when(transactionRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        TransactionResponse r1 = TransactionResponse.builder().id(1L).build();
        when(transactionMapper.toResponse(t1)).thenReturn(r1);

        PagedResponse<TransactionResponse> result = query.execute(null);

        assertEquals(List.of(r1), result.getContent());
        assertEquals(0, result.getPageNumber());
        verify(transactionRepository).findByUserId(eq(userId), any(Pageable.class));
    }
}
