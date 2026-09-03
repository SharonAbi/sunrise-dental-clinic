package com.sunrise.dental.dao;

import com.sunrise.dental.model.Patient;

import java.sql.SQLException;

public interface PatientDAO {

    Patient save(Patient patient) throws SQLException;

    Patient findById(int id) throws SQLException;
}
