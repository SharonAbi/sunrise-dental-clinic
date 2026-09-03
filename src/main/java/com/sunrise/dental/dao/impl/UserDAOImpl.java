package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.UserDAO;
import com.sunrise.dental.db.DBConnection;
import com.sunrise.dental.model.Role;
import com.sunrise.dental.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAOImpl implements UserDAO {

    private static final String FIND_BY_USERNAME_SQL =
            "SELECT id, username, password_hash, full_name, role FROM users WHERE username = ?";

    @Override
    public User findByUsername(String username) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_USERNAME_SQL)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setFullName(rs.getString("full_name"));
                    user.setRole(Role.valueOf(rs.getString("role")));
                    return user;
                }
                return null;
            }
        }
    }
}
