package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.UserRegistrationRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.exception.user.DuplicateEmailException;
import com.dimitar.financetracker.exception.user.DuplicateUsernameException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class CreateUserCommand implements Command<UserRegistrationRequest, UserResponse> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public CreateUserCommand(UserRepository userRepository,
                             UserMapper userMapper,
                             PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse execute(UserRegistrationRequest input) {
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new DuplicateUsernameException("Username already exists: " + input.getUsername());
        }

        if (userRepository.existsByEmail(input.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + input.getEmail());
        }

        User user = userMapper.toEntity(input);
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);

    }
}
