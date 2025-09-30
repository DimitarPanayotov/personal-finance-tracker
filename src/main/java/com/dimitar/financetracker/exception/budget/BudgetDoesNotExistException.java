package com.dimitar.financetracker.exception.budget;

public class BudgetDoesNotExistException extends RuntimeException {
    public BudgetDoesNotExistException(String message) {
        super(message);
    }
}

