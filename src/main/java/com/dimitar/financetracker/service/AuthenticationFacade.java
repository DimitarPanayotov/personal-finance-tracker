package com.dimitar.financetracker.service;

import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

//Request flow (tying it together)
//A request with a JWT hits the app.
//JwtAuthenticationFilter validates the token and sets the Authentication in SecurityContextHolder.
//Later in the service/controller, call authenticationFacade.getAuthenticatedUser().
//This method reads the Authentication object, gets the username/email,
//looks it up in the DB, and returns the full User entity.
//If we only need the ID, getAuthenticatedUserId() gives it directly.
@Service
@RequiredArgsConstructor
public class AuthenticationFacade { //facade = wrapper around Spring Security’s authentication system

    private final UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String loginIdentifier = authentication.getName();
        return userRepository.findByUsername(loginIdentifier)
                .or(() -> userRepository.findByEmail(loginIdentifier))
                .orElseThrow(() -> new UserDoesNotExistException("Authenticated user not found: " + loginIdentifier));
    }

    public Long getAuthenticatedUserId() {
        return getAuthenticatedUser().getId();
    }
}
