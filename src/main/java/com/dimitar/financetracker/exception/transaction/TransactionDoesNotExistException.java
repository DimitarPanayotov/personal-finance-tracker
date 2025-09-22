package com.dimitar.financetracker.exception.transaction;

public class TransactionDoesNotExistException extends RuntimeException {
    public TransactionDoesNotExistException(String message) {
        super(message);
    }
}
