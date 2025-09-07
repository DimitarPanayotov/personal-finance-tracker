package com.dimitar.financetracker.service.command.transaction.input;

import com.dimitar.financetracker.dto.request.transaction.CreateTransactionRequest;

public record CreateTransactionCommandInput(CreateTransactionRequest request, Long userId) {

}
