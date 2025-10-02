package com.dimitar.financetracker.service.query.user;

import com.dimitar.financetracker.dto.mapper.UserMapper;
import com.dimitar.financetracker.dto.response.user.UserResponse;
import com.dimitar.financetracker.entity.User;
import com.dimitar.financetracker.service.AuthenticationFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserQueryTest {

    @Mock
    private AuthenticationFacade authenticationFacade;
    @Mock
    private UserMapper userMapper;

    private GetUserQuery query;

    @BeforeEach
    void setUp() {
        query = new GetUserQuery(authenticationFacade, userMapper);
    }

    @Test
    void execute_returnsMappedUserResponse() {
        // Arrange
        User user = User.builder().id(1L).username("john").email("john@example.com").build();
        UserResponse expected = UserResponse.builder().id(1L).username("john").email("john@example.com").build();

        when(authenticationFacade.getAuthenticatedUser()).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(expected);

        // Act
        UserResponse actual = query.execute(null);

        // Assert
        assertEquals(expected, actual);
        verify(authenticationFacade).getAuthenticatedUser();
        verify(userMapper).toResponse(user);
        verifyNoMoreInteractions(authenticationFacade, userMapper);
    }
}

