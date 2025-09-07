package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.IncorrectPasswordException;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class ChangePasswordCommand implements Command<PasswordChangeRequest, UserResponse> {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse execute(PasswordChangeRequest input) {
        // Find the user by ID
        User existingUser = userRepository.findById(input.getUserId())
            .orElseThrow(() -> new UserDoesNotExistException("User with this id does not exist: " + input.getUserId()));

        // Verify the current password is correct
        if (!passwordEncoder.matches(input.getPassword(), existingUser.getPassword())) {
            throw new IncorrectPasswordException("Current password is incorrect");
        }

        // Encode and set the new password
        String encodedNewPassword = passwordEncoder.encode(input.getNewPassword());
        existingUser.setPassword(encodedNewPassword);

        // Save the updated user
        User updatedUser = userRepository.save(existingUser);

        // Return the user response using mapper
        return userMapper.toResponse(updatedUser);
    }
}
