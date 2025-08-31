package com.dimitar.financetracker.service.query.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.exception.user.UserDoesNotExistException;
import com.dimitar.financetracker.repository.UserRepository;
import com.dimitar.financetracker.service.query.Query;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetUserQuery implements Query<Long, UserResponse> {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public GetUserQuery(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse execute(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userMapper.toResponse(userOptional.get());
        } else {
            throw new UserDoesNotExistException("User with this id does not exist: " + id);
        }
    }
}
