package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.user.UserLoginRequest;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.AuthenticationResponse;
import com.dimitar.financetracker.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns authentication tokens (e.g., JWT)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied registration data"),
            @ApiResponse(responseCode = "409", description = "Conflict - user with provided unique field already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody UserRegistrationRequest request) {
        AuthenticationResponse response = authenticationService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Authenticate user (login)",
            description = "Authenticates a user with credentials and returns authentication tokens (e.g., JWT)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Validation failed for supplied login data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody UserLoginRequest request) {
        AuthenticationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}
