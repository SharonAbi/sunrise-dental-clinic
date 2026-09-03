package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.TreatmentDAO;
import com.sunrise.dental.db.DBConnection;
import com.sunrise.dental.model.Treatment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreatmentDAOImpl implements TreatmentDAO {

    private static final String FIND_ALL_SQL =
            "SELECT id, name, fee FROM treatments ORDER BY name";
    private static final String FIND_BY_ID_SQL =
            "SELECT id, name, fee FROM treatments WHERE id = ?";

    @Override
    public List<Treatment> findAll() throws SQLException {
        List<Treatment> treatments = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                treatments.add(mapRow(rs));
            }
        }
        return treatments;
    }

    @Override
    public Treatment findById(int id) throws SQLException {
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

    private Treatment mapRow(ResultSet rs) throws SQLException {
        return new Treatment(rs.getInt("id"), rs.getString("name"), rs.getDouble("fee"));
    }
}
