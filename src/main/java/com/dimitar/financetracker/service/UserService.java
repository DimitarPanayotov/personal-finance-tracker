package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.service.command.user.CreateUserCommand;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CreateUserCommand createUserCommand;

    public UserService(CreateUserCommand createUserCommand) {
        this.createUserCommand = createUserCommand;
    }

    public UserResponse createUser(UserRegistrationRequest request) {
        return createUserCommand.execute(request);
    }
}
