package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserCommandTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private CreateUserCommand command;

    @BeforeEach
    void setUp() {
        command = new CreateUserCommand(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void execute_createsUser_whenNoDuplicates() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("plain")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);

        User mapped = User.builder().username("john").email("john@example.com").build();
        when(userMapper.toEntity(request)).thenReturn(mapped);

        when(passwordEncoder.encode("plain")).thenReturn("hashed");

        //We use ArgumentCaptor when we want to assert on the actual objects passed to mocked methods
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse expected = UserResponse.builder().id(1L).username("john").email("john@example.com").build();
        when(userMapper.toResponse(any(User.class))).thenReturn(expected);

        UserResponse actual = command.execute(request);

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@example.com");
        verify(userMapper).toEntity(request);
        verify(passwordEncoder).encode("plain");
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();
        assertEquals("hashed", saved.getPassword(), "Password should be encoded before saving");
        assertEquals("john", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals(expected, actual);
    }

    @Test
    void execute_throwsWhenUsernameExists() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("plain")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(DuplicateUsernameException.class, () -> command.execute(request));
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void execute_throwsWhenEmailExists() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("plain")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> command.execute(request));
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper, passwordEncoder);
    }
}

