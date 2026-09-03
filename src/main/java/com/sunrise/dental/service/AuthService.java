package com.sunrise.dental.service;

import com.sunrise.dental.dao.DAOFactory;
import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.model.User;
import com.sunrise.dental.util.PasswordUtil;

import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this(DAOFactory.getUserDAO());
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String plainPassword) throws SQLException {
        if (username == null || plainPassword == null) {
            return null;
        }
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
        String hashed = PasswordUtil.hash(plainPassword);
        return hashed.equals(user.getPasswordHash()) ? user : null;
    }
}
