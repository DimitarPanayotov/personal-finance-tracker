package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.service.command.user.CreateUserCommand;
import com.dimitar.financetracker.service.query.user.GetUserQuery;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final CreateUserCommand createUserCommand;
    private final GetUserQuery getUserQuery;

    public UserService(CreateUserCommand createUserCommand,
                       GetUserQuery getUserQuery) {
        this.createUserCommand = createUserCommand;
        this.getUserQuery = getUserQuery;
    }

    public UserResponse createUser(UserRegistrationRequest request) {
        return createUserCommand.execute(request);
    }

    public UserResponse getUser(Long id) {
        return getUserQuery.execute(id);
    }
}
