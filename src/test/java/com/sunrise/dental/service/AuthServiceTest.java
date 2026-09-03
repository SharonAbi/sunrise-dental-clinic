package com.sunrise.dental.service;

import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.model.Role;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDAO userDAO;

    @Test
    void authenticate_returnsUser_whenPasswordMatchesStoredHash() throws SQLException {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash(PasswordUtil.hash("admin123"));
        user.setRole(Role.ADMIN);
        when(userDAO.findByUsername("admin")).thenReturn(user);

        AuthService authService = new AuthService(userDAO);
        User result = authService.authenticate("admin", "admin123");

        assertEquals(user, result);
    }

    @Test
    void authenticate_returnsNull_whenPasswordIsWrong() throws SQLException {
        User user = new User();
        user.setUsername("admin");
        user.setPasswordHash(PasswordUtil.hash("admin123"));
        when(userDAO.findByUsername("admin")).thenReturn(user);

        AuthService authService = new AuthService(userDAO);
        assertNull(authService.authenticate("admin", "wrong-password"));
    }

    @Test
    void authenticate_returnsNull_whenUsernameDoesNotExist() throws SQLException {
        when(userDAO.findByUsername("ghost")).thenReturn(null);

        AuthService authService = new AuthService(userDAO);
        assertNull(authService.authenticate("ghost", "anything"));
    }
}
