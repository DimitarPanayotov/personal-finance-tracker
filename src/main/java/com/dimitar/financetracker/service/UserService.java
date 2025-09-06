package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.service.command.user.CreateUserCommand;
import com.dimitar.financetracker.service.command.user.DeleteUserCommand;
import com.dimitar.financetracker.service.command.user.UpdateUserCommand;
import com.dimitar.financetracker.service.query.user.GetUserQuery;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final GetUserQuery getUserQuery;
    private final UpdateUserCommand updateUserCommand;
    private final DeleteUserCommand deleteUserCommand;

    public UserService(GetUserQuery getUserQuery,
                       UpdateUserCommand updateUserCommand,
                       DeleteUserCommand deleteUserCommand) {
        this.getUserQuery = getUserQuery;
        this.updateUserCommand = updateUserCommand;
        this.deleteUserCommand = deleteUserCommand;
    }

    public UserResponse getUser(Long id) {
        return getUserQuery.execute(id);
    }

    public UserResponse updateUser(UserUpdateRequest request) {
        return updateUserCommand.execute(request);
    }
    
    public void deleteUser(Long userId) {
        deleteUserCommand.execute(userId);
    }
}
