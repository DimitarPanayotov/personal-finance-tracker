package com.dimitar.financetracker.service.query.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.service.AuthenticationFacade;
import com.dimitar.financetracker.service.query.Query;
import org.springframework.stereotype.Component;

@Component
public class GetUserQuery implements Query<Void, UserResponse> {
    private final AuthenticationFacade authenticationFacade;
    private final UserMapper userMapper;

    public GetUserQuery(AuthenticationFacade authenticationFacade, UserMapper userMapper) {
        this.authenticationFacade = authenticationFacade;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse execute(Void input) {
        User user = authenticationFacade.getAuthenticatedUser();
        return userMapper.toResponse(user);
    }
}
