package com.dimitar.financetracker.service.query;

public interface Query<I, O> {
    O execute(I input);
}
