package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.request.user.PasswordChangeRequest;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.IncorrectPasswordException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Transactional
@RequiredArgsConstructor
public class ChangePasswordCommand implements Command<PasswordChangeRequest, UserResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserResponse execute(PasswordChangeRequest input) {
        User user = authenticationFacade.getAuthenticatedUser();

        if (!passwordEncoder.matches(input.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("Current password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(input.getNewPassword());
        user.setPassword(encodedNewPassword);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }
}
