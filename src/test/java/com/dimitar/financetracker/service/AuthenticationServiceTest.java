package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.UserLoginRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserAlreadyExistsException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private CustomUserDetailsService userDetailsService;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                jwtUtil,
                authenticationManager,
                userDetailsService
        );
    }

    @Test
    void register_createsUserAndReturnsToken() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("secret")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("hashed");
        // repository save can return the same user instance
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("hashed")
                .authorities(new java.util.ArrayList<>())
                .build();
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        AuthenticationResponse response = authenticationService.register(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(86400L, response.getExpiresIn());

        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
        verify(userDetailsService).loadUserByUsername("john");
        verify(jwtUtil).generateToken(userDetails);
        verifyNoMoreInteractions(userRepository, passwordEncoder, userDetailsService, jwtUtil);
        verifyNoInteractions(authenticationManager);
    }

    @Test
    void register_throwsWhenUsernameExists() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("secret")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));
        verify(userRepository).existsByUsername("john");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, jwtUtil, authenticationManager, userDetailsService);
    }

    @Test
    void register_throwsWhenEmailExists() {
        UserRegistrationRequest request = UserRegistrationRequest.builder()
                .username("john")
                .email("john@example.com")
                .password("secret")
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authenticationService.register(request));
        verify(userRepository).existsByUsername("john");
        verify(userRepository).existsByEmail("john@example.com");
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, jwtUtil, authenticationManager, userDetailsService);
    }

    @Test
    void login_authenticatesAndReturnsToken() {
        UserLoginRequest request = UserLoginRequest.builder()
                .usernameOrEmail("john")
                .password("secret")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("john")
                .password("hashed")
                .authorities(new java.util.ArrayList<>())
                .build();
        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail("john", "john"))
                .thenReturn(Optional.of(User.builder().username("john").email("john@example.com").password("hashed").build()));

        AuthenticationResponse response = authenticationService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getType());
        assertEquals("john", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(86400L, response.getExpiresIn());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("john");
        verify(jwtUtil).generateToken(userDetails);
        verify(userRepository).findByUsernameOrEmail("john", "john");
        verifyNoMoreInteractions(authenticationManager, userDetailsService, jwtUtil, userRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_throwsWhenUserNotFound() {
        UserLoginRequest request = UserLoginRequest.builder()
                .usernameOrEmail("john")
                .password("secret")
                .build();

        // authenticate succeeds, but repository does not find the user
        when(userDetailsService.loadUserByUsername("john")).thenReturn(
                org.springframework.security.core.userdetails.User
                        .withUsername("john").password("hashed").authorities(new java.util.ArrayList<>()).build());
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt-token");
        when(userRepository.findByUsernameOrEmail("john", "john")).thenReturn(Optional.empty());

        assertThrows(UserDoesNotExistException.class, () -> authenticationService.login(request));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername("john");
        verify(jwtUtil).generateToken(any(UserDetails.class));
        verify(userRepository).findByUsernameOrEmail("john", "john");
        verifyNoMoreInteractions(authenticationManager, userDetailsService, jwtUtil, userRepository);
        verifyNoInteractions(passwordEncoder);
    }
}
