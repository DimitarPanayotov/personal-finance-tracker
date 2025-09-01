package com.dimitar.financetracker.exception.category;

public class CategoryDoesNotExistException extends RuntimeException {
    public CategoryDoesNotExistException(String message) {
        super(message);
    }
}
