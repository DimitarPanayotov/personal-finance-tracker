package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserCommandTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private UpdateUserCommand command;

    @BeforeEach
    void setUp() {
        command = new UpdateUserCommand(authenticationFacade, userRepository, userMapper);
    }

    @Test
    void execute_updatesUsernameAndEmail_whenChangedAndUnique() {
        // Arrange
        User user = User.builder().id(1L).username("john").email("old@example.com").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("johnny")
                .email("new@example.com")
                .build();

        when(userRepository.existsByUsername("johnny")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse expected = UserResponse.builder().id(1L).username("johnny").email("new@example.com").build();
        when(userMapper.toResponse(any(User.class))).thenReturn(expected);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // Act
        UserResponse actual = command.execute(request);

        // Assert
        verify(userRepository).existsByUsername("johnny");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();
        assertEquals("johnny", saved.getUsername());
        assertEquals("new@example.com", saved.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    void execute_throwsWhenDuplicateUsername() {
        User user = User.builder().username("john").email("old@example.com").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("johnny")
                .build();

        when(userRepository.existsByUsername("johnny")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> command.execute(request));
        verify(userRepository, never()).save(any());
        verify(userRepository, never()).existsByEmail(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void execute_throwsWhenDuplicateEmail() {
        User user = User.builder().username("john").email("old@example.com").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);

        UserUpdateRequest request = UserUpdateRequest.builder()
                .email("new@example.com")
                .build();

        when(userRepository.existsByEmail("new@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> command.execute(request));
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper);
    }

    @Test
    void execute_savesWhenNoChangesProvided() {
        // Arrange
        User user = User.builder().id(1L).username("john").email("old@example.com").build();
        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);
        // Same values mean no change conditions will be triggered
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("john")
                .email("old@example.com")
                .build();

        when(userRepository.save(user)).thenReturn(user);
        UserResponse expected = UserResponse.builder().id(1L).username("john").email("old@example.com").build();
        when(userMapper.toResponse(user)).thenReturn(expected);

        // Act
        UserResponse actual = command.execute(request);

        // Assert
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(user);
        assertEquals(expected, actual);
    }
}

