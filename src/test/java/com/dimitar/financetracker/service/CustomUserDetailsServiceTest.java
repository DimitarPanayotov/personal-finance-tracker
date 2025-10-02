package com.dimitar.financetracker.service;

import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_returnsSpringUser() {
        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);
        User entity = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .password("hashed")
                .build();
        when(userRepository.findByUsernameOrEmail("john", "john")).thenReturn(Optional.of(entity));

        UserDetails details = service.loadUserByUsername("john");

        assertEquals("john", details.getUsername());
        assertEquals("hashed", details.getPassword());
        assertNotNull(details.getAuthorities());
        verify(userRepository).findByUsernameOrEmail("john", "john");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsername_throwsWhenNotFound() {
        CustomUserDetailsService service = new CustomUserDetailsService(userRepository);
        when(userRepository.findByUsernameOrEmail("missing", "missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
        verify(userRepository).findByUsernameOrEmail("missing", "missing");
        verifyNoMoreInteractions(userRepository);
    }
}

