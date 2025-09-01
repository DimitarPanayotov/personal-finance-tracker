package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.UserUpdateRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class UpdateUserCommand implements Command<UserUpdateRequest, UserResponse> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UpdateUserCommand(UserRepository userRepository,
                             UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse execute(UserUpdateRequest input) {
        if (input.getId() == null) {
            throw new IllegalArgumentException("User ID is required for update operation");
        }
        User existingUser = userRepository.findById(input.getId())
            .orElseThrow(() -> new UserDoesNotExistException("User with this id does not exist: " + input.getId()));

        if (input.getUsername() != null && !input.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(input.getUsername())) {
                throw new DuplicateUsernameException("Username already exists: " + input.getUsername());
            }
            existingUser.setUsername(input.getUsername());
        }

        if (input.getEmail() != null && !input.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(input.getEmail())) {
                throw new DuplicateEmailException("Email already exists: " + input.getEmail());
            }
            existingUser.setEmail(input.getEmail());
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toResponse(updatedUser);
    }
}
