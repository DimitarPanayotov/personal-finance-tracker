package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.dto.response.user.UserStatisticsResponse;
import com.dimitar.financetracker.service.command.user.ChangePasswordCommand;
import com.dimitar.financetracker.service.command.user.DeleteUserCommand;
import com.dimitar.financetracker.service.command.user.UpdateUserCommand;
import com.dimitar.financetracker.service.query.user.GetUserQuery;
import com.dimitar.financetracker.service.query.user.GetUserStatisticsQuery;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final GetUserQuery getUserQuery;
    private final UpdateUserCommand updateUserCommand;
    private final DeleteUserCommand deleteUserCommand;
    private final ChangePasswordCommand changePasswordCommand;
    private final GetUserStatisticsQuery getUserStatisticsQuery;

    public UserService(GetUserQuery getUserQuery,
                       UpdateUserCommand updateUserCommand,
                       DeleteUserCommand deleteUserCommand,
                       ChangePasswordCommand changePasswordCommand,
                       GetUserStatisticsQuery getUserStatisticsQuery) {
        this.getUserQuery = getUserQuery;
        this.updateUserCommand = updateUserCommand;
        this.deleteUserCommand = deleteUserCommand;
        this.changePasswordCommand = changePasswordCommand;
        this.getUserStatisticsQuery = getUserStatisticsQuery;
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

    public UserStatisticsResponse getStatistics() {return getUserStatisticsQuery.execute(null); }
}
