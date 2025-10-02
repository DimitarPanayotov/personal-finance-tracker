package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteUserCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private UserRepository userRepository;

    private DeleteUserCommand command;

    @BeforeEach
    void setUp() {
        command = new DeleteUserCommand(authenticationFacade, userRepository);
    }

    @Test
    void execute_deletesAuthenticatedUser() {
        // Arrange
        User user = User.builder().id(1L).username("john").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        // Act
        command.execute(null);

        // Assert
        verify(userRepository).delete(user);
        verifyNoMoreInteractions(userRepository);
    }
}

