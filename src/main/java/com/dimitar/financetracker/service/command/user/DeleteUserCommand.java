package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class DeleteUserCommand implements Command<Long, Void> {
    private final UserRepository userRepository;

    public DeleteUserCommand(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(Long input) {
        User user = userRepository.findById(input)
            .orElseThrow(() -> new UserDoesNotExistException("User with this id does not exist: " + input));

        userRepository.delete(user);
        return null;
    }
}
