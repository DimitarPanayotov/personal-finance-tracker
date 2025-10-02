package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class UpdateUserCommand implements Command<UserUpdateRequest, UserResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UpdateUserCommand(AuthenticationFacade authenticationFacade,
                             UserRepository userRepository,
                             UserMapper userMapper) {
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse execute(UserUpdateRequest request) {
        User user = authenticationFacade.getAuthenticatedUser();

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateUsernameException("Username already exists: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateEmailException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }
}
