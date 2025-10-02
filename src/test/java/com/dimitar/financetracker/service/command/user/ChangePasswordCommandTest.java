package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.IncorrectPasswordException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePasswordCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;

    private ChangePasswordCommand command;

    @BeforeEach
    void setUp() {
        command = new ChangePasswordCommand(authenticationFacade, userRepository, passwordEncoder, userMapper);
    }

    @Test
    void execute_changesPassword_whenCurrentPasswordMatches() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .password("hashedOld")
                .build();
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .password("old")
                .newPassword("newSecret!")
                .build();

        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches("old", "hashedOld")).thenReturn(true);
        when(passwordEncoder.encode("newSecret!")).thenReturn("hashedNew");
        // Repository will be called with the same user instance
        when(userRepository.save(user)).thenReturn(user);

        UserResponse expected = UserResponse.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .build();
        when(userMapper.toResponse(user)).thenReturn(expected);

        // Act
        UserResponse actual = command.execute(request);

        // Assert
        assertEquals(expected, actual);
        assertEquals("hashedNew", user.getPassword(), "User password should be updated before save");
        verify(userRepository, times(1)).save(user);
        verify(passwordEncoder).encode("newSecret!");
        verify(userMapper).toResponse(user);
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void execute_throwsWhenCurrentPasswordIncorrect() {
        // Arrange
        User user = User.builder().password("hashedOld").build();
        PasswordChangeRequest request = PasswordChangeRequest.builder()
                .password("wrong")
                .newPassword("newSecret!")
                .build();

        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);
        when(passwordEncoder.matches("wrong", "hashedOld")).thenReturn(false);

        // Act + Assert
        assertThrows(IncorrectPasswordException.class, () -> command.execute(request));
        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
        verifyNoInteractions(userMapper);
    }
}
