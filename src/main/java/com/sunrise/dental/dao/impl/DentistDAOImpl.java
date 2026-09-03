package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.DentistDAO;
import com.sunrise.dental.db.DBConnection;
import com.sunrise.dental.model.Dentist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DentistDAOImpl implements DentistDAO {

    private static final String FIND_ALL_SQL =
            "SELECT id, name, specialization FROM dentists ORDER BY name";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, name, specialization FROM dentists WHERE id = ?";

    @Override
    public List<Dentist> findAll() throws SQLException {
        List<Dentist> dentists = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                dentists.add(mapRow(rs));
            }
        }
        return dentists;
    }

    @Override
    public Dentist findById(int id) throws SQLException {
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

    private Dentist mapRow(ResultSet rs) throws SQLException {
        return new Dentist(rs.getInt("id"), rs.getString("name"), rs.getString("specialization"));
    }
}
