package com.dimitar.financetracker.controller;

import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        UserResponse response = userService.getUser(authenticatedUserId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@Valid @RequestBody UserUpdateRequest request) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        request.setId(authenticatedUserId);
        UserResponse response = userService.updateUser(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        userService.deleteUser(authenticatedUserId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @PatchMapping("/me/change-password")
    public ResponseEntity<UserResponse> changeCurrentUserPassword(@Valid @RequestBody PasswordChangeRequest request) {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();
        request.setUserId(authenticatedUserId);
        UserResponse response = userService.changePassword(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
