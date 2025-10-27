package com.dimitar.financetracker.service.query.transaction;

import com.dimitar.financetracker.dto.mapper.TransactionMapper;
import com.dimitar.financetracker.dto.request.PageRequest;
import com.dimitar.financetracker.dto.response.PagedResponse;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.entity.Transaction;
import com.dimitar.financetracker.repository.TransactionRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllTransactionsQuery implements Query<PageRequest, PagedResponse<TransactionResponse>> {
    private final AuthenticationFacade authenticationFacade;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> execute(PageRequest pageRequest) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        // Handle null pageRequest (backward compatibility)
        if (pageRequest == null) {
            pageRequest = PageRequest.builder().build();
        }

        org.springframework.data.domain.Page<Transaction> page = transactionRepository.findByUserId(
            authenticatedUserId,
            pageRequest.toPageable()
        );

        List<TransactionResponse> content = page.getContent().stream()
            .map(transactionMapper::toResponse)
            .toList();

        return PagedResponse.<TransactionResponse>builder()
            .content(content)
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .first(page.isFirst())
            .empty(page.isEmpty())
            .build();
    }
}
