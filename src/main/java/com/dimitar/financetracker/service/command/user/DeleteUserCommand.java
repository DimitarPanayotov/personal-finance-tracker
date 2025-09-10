package com.dimitar.financetracker.service.command.user;

import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.command.Command;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class DeleteUserCommand implements Command<Void, Void> {
    private final AuthenticationFacade authenticationFacade;
    private final UserRepository userRepository;

    public DeleteUserCommand(AuthenticationFacade authenticationFacade, UserRepository userRepository) {
        this.authenticationFacade = authenticationFacade;
        this.userRepository = userRepository;
    }

    @Override
    public Void execute(Void input) {
        User user = authenticationFacade.getAuthenticatedUser();
        userRepository.delete(user);
        return null;
    }
}
