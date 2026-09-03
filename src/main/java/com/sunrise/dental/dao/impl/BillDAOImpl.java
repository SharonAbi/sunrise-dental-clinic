package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.BillDAO;
import com.sunrise.dental.db.DBConnection;
import com.sunrise.dental.model.Bill;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class BillDAOImpl implements BillDAO {

    private static final String INSERT_SQL =
            "INSERT INTO bills (appointment_id, consultation_fee, treatment_fee, discount_amount, " +
                    "total_amount, insurance_applied, generated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_APPOINTMENT_ID_SQL =
            "SELECT id, appointment_id, consultation_fee, treatment_fee, discount_amount, total_amount, " +
                    "insurance_applied, generated_at FROM bills WHERE appointment_id = ?";

    @Override
    public Bill save(Bill bill) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, bill.getAppointment().getId());
            ps.setDouble(2, bill.getConsultationFee());
            ps.setDouble(3, bill.getTreatmentFee());
            ps.setDouble(4, bill.getDiscountAmount());
            ps.setDouble(5, bill.getTotalAmount());
            ps.setBoolean(6, bill.isInsuranceApplied());
            ps.setTimestamp(7, Timestamp.valueOf(bill.getGeneratedAt()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    bill.setId(keys.getInt(1));
                }
            }
            return bill;
        }
    }

    @Override
    public Bill findByAppointmentId(int appointmentId) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_APPOINTMENT_ID_SQL)) {

            ps.setInt(1, appointmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        bill.setConsultationFee(rs.getDouble("consultation_fee"));
        bill.setTreatmentFee(rs.getDouble("treatment_fee"));
        bill.setDiscountAmount(rs.getDouble("discount_amount"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setInsuranceApplied(rs.getBoolean("insurance_applied"));
        bill.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        // Note: the appointment association is intentionally left for the
        // caller (BillService) to attach, since it already holds the
        // Appointment used to look this bill up - avoids a redundant join.
        return bill;
    }
}
