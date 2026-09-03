package com.sunrise.dental.dao;

import com.sunrise.dental.model.Treatment;

import java.sql.SQLException;
import java.util.List;

public interface TreatmentDAO {

    List<Treatment> findAll() throws SQLException;

    Treatment findById(int id) throws SQLException;
}
