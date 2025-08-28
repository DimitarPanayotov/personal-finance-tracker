package com.dimitar.financetracker.service.command;

public interface Command<I, O> {
    O execute(I input);
}
