package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.service.command.user.ChangePasswordCommand;
import com.dimitar.financetracker.service.command.user.DeleteUserCommand;
import com.dimitar.financetracker.service.command.user.UpdateUserCommand;
import com.dimitar.financetracker.service.query.user.GetUserQuery;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final GetUserQuery getUserQuery;
    private final UpdateUserCommand updateUserCommand;
    private final DeleteUserCommand deleteUserCommand;
    private final ChangePasswordCommand changePasswordCommand;

    public UserService(GetUserQuery getUserQuery,
                       UpdateUserCommand updateUserCommand,
                       DeleteUserCommand deleteUserCommand,
                       ChangePasswordCommand changePasswordCommand) {
        this.getUserQuery = getUserQuery;
        this.updateUserCommand = updateUserCommand;
        this.deleteUserCommand = deleteUserCommand;
        this.changePasswordCommand = changePasswordCommand;
    }

    public UserResponse getUser() {
        return getUserQuery.execute(null);
    }

    public UserResponse updateUser(UserUpdateRequest request) {
        return updateUserCommand.execute(request);
    }
    
    public void deleteUser() {
        deleteUserCommand.execute(null);
    }

    public UserResponse changePassword(PasswordChangeRequest request) {
        return changePasswordCommand.execute(request);
    }
}
