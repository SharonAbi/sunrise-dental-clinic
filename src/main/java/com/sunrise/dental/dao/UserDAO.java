package com.sunrise.dental.dao;

import com.sunrise.dental.model.User;

import java.sql.SQLException;

public interface UserDAO {

    User findByUsername(String username) throws SQLException;
}
