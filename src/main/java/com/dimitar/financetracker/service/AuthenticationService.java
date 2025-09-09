package com.dimitar.financetracker.service;

import com.dimitar.financetracker.dto.request.user.UserLoginRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserAlreadyExistsException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//Request flow (tying it all together)
//Register (POST /api/auth/register)
//Validate uniqueness of username/email.
//Save new user with encoded password.
//Generate JWT and return it.
//Login (POST /api/auth/login)
//Authenticate credentials via AuthenticationManager.
//Generate JWT.
//Return JWT + user info.
//Subsequent requests
//Client includes Authorization: Bearer <jwt> header.
//JwtAuthenticationFilter validates token and sets authentication.
//Controllers can then access @AuthenticationPrincipal or your AuthenticationFacade to get the user.
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final long ONE_DAY_IN_MILLIS = 86400L;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthenticationResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtUtil.generateToken(userDetails);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .expiresIn(ONE_DAY_IN_MILLIS)
                .build();
    }

    public AuthenticationResponse login(UserLoginRequest request) {
        // Authenticate user
        //Behind the scenes:
        //Delegates to DaoAuthenticationProvider.
        //Calls CustomUserDetailsService.loadUserByUsername(...).
        //Verifies password with PasswordEncoder.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        // Load user details and generate token
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsernameOrEmail());
        String jwtToken = jwtUtil.generateToken(userDetails);

        // Get user info
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail(), request.getUsernameOrEmail())
                .orElseThrow(() -> new UserDoesNotExistException("User not found"));

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .expiresIn(ONE_DAY_IN_MILLIS)
                .build();
    }
}
