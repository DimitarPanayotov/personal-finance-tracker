package com.dimitar.financetracker.service;

import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFacadeTest {

    @Mock private UserRepository userRepository;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAuthenticatedUser_returnsUser_whenAuthenticatedAndFoundByUsername() {
        // Arrange
        User expected = User.builder().id(1L).username("john").email("john@example.com").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(expected));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        AuthenticationFacade facade = new AuthenticationFacade(userRepository);

        // Act
        User actual = facade.getAuthenticatedUser();

        // Assert
        assertEquals(expected, actual);
        verify(userRepository).findByUsername("john");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUser_returnsUser_whenAuthenticatedAndFoundByEmail() {
        // Arrange
        User expected = User.builder().id(2L).username("jane").email("jane@example.com").build();
        when(userRepository.findByUsername("jane@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(expected));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("jane@example.com");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        AuthenticationFacade facade = new AuthenticationFacade(userRepository);

        // Act
        User actual = facade.getAuthenticatedUser();

        // Assert
        assertEquals(expected, actual);
        verify(userRepository).findByUsername("jane@example.com");
        verify(userRepository).findByEmail("jane@example.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUser_throws_whenNotAuthenticated() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        AuthenticationFacade facade = new AuthenticationFacade(userRepository);

        // Act + Assert
        assertThrows(RuntimeException.class, facade::getAuthenticatedUser);
        verifyNoInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUser_throws_whenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("ghost")).thenReturn(Optional.empty());

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("ghost");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        AuthenticationFacade facade = new AuthenticationFacade(userRepository);

        // Act + Assert
        assertThrows(UserDoesNotExistException.class, facade::getAuthenticatedUser);
        verify(userRepository).findByUsername("ghost");
        verify(userRepository).findByEmail("ghost");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getAuthenticatedUserId_returnsId() {
        // Arrange
        User expected = User.builder().id(42L).username("john").build();
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(expected));

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("john");
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);

        AuthenticationFacade facade = new AuthenticationFacade(userRepository);

        // Act
        Long id = facade.getAuthenticatedUserId();

        // Assert
        assertEquals(42L, id);
        verify(userRepository).findByUsername("john");
        verifyNoMoreInteractions(userRepository);
    }
}

