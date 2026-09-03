package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.PatientDAO;
import com.sunrise.dental.db.DBConnection;
import com.sunrise.dental.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatientDAOImpl implements PatientDAO {

    private static final String INSERT_SQL =
            "INSERT INTO patients (name, address, contact_number) VALUES (?, ?, ?)";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, name, address, contact_number FROM patients WHERE id = ?";

    @Override
    public Patient save(Patient patient) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patient.getName());
            ps.setString(2, patient.getAddress());
            ps.setString(3, patient.getContactNumber());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    patient.setId(keys.getInt(1));
                }
            }
            return patient;
        }
    }

    @Override
    public Patient findById(int id) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setName(rs.getString("name"));
        patient.setAddress(rs.getString("address"));
        patient.setContactNumber(rs.getString("contact_number"));
        return patient;
    }
}
