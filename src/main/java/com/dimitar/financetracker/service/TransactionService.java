package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;
import com.dimitar.financetracker.dto.response.transaction.TransactionResponse;
import com.dimitar.financetracker.service.command.transaction.CreateTransactionCommand;
import com.dimitar.financetracker.service.query.transaction.GetAllTransactionsQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CreateTransactionCommand createTransactionCommand;
    private final GetAllTransactionsQuery getAllTransactionsQuery;

    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        return createTransactionCommand.execute(request);
    }

    public List<TransactionResponse> getAllTransactions() {
        return getAllTransactionsQuery.execute(null);
    }
}
