package com.sunrise.dental.dao;

import com.sunrise.dental.model.Dentist;

import java.sql.SQLException;
import java.util.List;

public interface DentistDAO {

    List<Dentist> findAll() throws SQLException;

    Dentist findById(int id) throws SQLException;
}
