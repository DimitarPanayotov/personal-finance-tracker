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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private GetUserQuery getUserQuery;
    @Mock
    private UpdateUserCommand updateUserCommand;
    @Mock
    private DeleteUserCommand deleteUserCommand;
    @Mock
    private ChangePasswordCommand changePasswordCommand;
    @Mock
    private GetUserStatisticsQuery getUserStatisticsQuery;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                getUserQuery,
                updateUserCommand,
                deleteUserCommand,
                changePasswordCommand,
                getUserStatisticsQuery
        );
    }

    @Test
    void getUser_returnsUserResponseFromQuery() {
        // Arrange
        UserResponse expected = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .build();
        when(getUserQuery.execute(null)).thenReturn(expected);

        // Act
        UserResponse actual = userService.getUser();

        // Assert
        assertEquals(expected, actual);
        verify(getUserQuery, times(1)).execute(null);
        verifyNoMoreInteractions(getUserQuery);
        verifyNoInteractions(updateUserCommand, deleteUserCommand, changePasswordCommand, getUserStatisticsQuery);
    }

    @Test
    void updateUser_delegatesToCommand() {
        // Arrange
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("newName")
                .email("new@example.com")
                .build();
        UserResponse expected = UserResponse.builder()
                .id(1L)
                .username("newName")
                .email("new@example.com")
                .build();
        when(updateUserCommand.execute(request)).thenReturn(expected);

        // Act
        UserResponse actual = userService.updateUser(request);

        // Assert
        assertEquals(expected, actual);
        verify(updateUserCommand, times(1)).execute(request);
        verifyNoMoreInteractions(updateUserCommand);
        verifyNoInteractions(getUserQuery, deleteUserCommand, changePasswordCommand, getUserStatisticsQuery);
    }

    @Test
    void deleteUser_delegatesToCommand() {
        // Act
        userService.deleteUser();

        // Assert
        verify(deleteUserCommand, times(1)).execute(null);
        verifyNoMoreInteractions(deleteUserCommand);
        verifyNoInteractions(getUserQuery, updateUserCommand, changePasswordCommand, getUserStatisticsQuery);
    }

    @Test
    void changePassword_delegatesToCommand() {
        // Arrange
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .password("old")
                .newPassword("new")
                .build();
        UserResponse expected = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .build();
        when(changePasswordCommand.execute(request)).thenReturn(expected);

        // Act
        UserResponse actual = userService.changePassword(request);

        // Assert
        assertEquals(expected, actual);
        verify(changePasswordCommand, times(1)).execute(request);
        verifyNoMoreInteractions(changePasswordCommand);
        verifyNoInteractions(getUserQuery, updateUserCommand, deleteUserCommand, getUserStatisticsQuery);
    }

    @Test
    void getStatistics_returnsFromQuery() {
        // Arrange
        UserStatisticsResponse expected = UserStatisticsResponse.builder()
                .totalIncome(java.math.BigDecimal.TEN)
                .totalExpenses(java.math.BigDecimal.ONE)
                .netBalance(java.math.BigDecimal.valueOf(9))
                .totalTransactions(5L)
                .totalIncomeTransactions(3L)
                .totalExpenseTransactions(2L)
                .averageIncomePerTransaction(java.math.BigDecimal.valueOf(3.33))
                .averageExpensePerTransaction(java.math.BigDecimal.valueOf(0.5))
                .monthlyIncome(java.math.BigDecimal.TEN)
                .monthlyExpenses(java.math.BigDecimal.ONE)
                .monthlyNetBalance(java.math.BigDecimal.valueOf(9))
                .build();
        when(getUserStatisticsQuery.execute(null)).thenReturn(expected);

        // Act
        UserStatisticsResponse actual = userService.getStatistics();

        // Assert
        assertEquals(expected, actual);
        verify(getUserStatisticsQuery, times(1)).execute(null);
        verifyNoMoreInteractions(getUserStatisticsQuery);
        verifyNoInteractions(getUserQuery, updateUserCommand, deleteUserCommand, changePasswordCommand);
    }
}

