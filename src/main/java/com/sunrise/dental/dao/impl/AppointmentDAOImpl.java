package com.sunrise.dental.dao.impl;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.db.DBConnection;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.AppointmentStatus;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.Treatment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAOImpl implements AppointmentDAO {

    private static final String INSERT_SQL =
            "INSERT INTO appointments (appointment_number, patient_id, dentist_id, treatment_id, " +
                    "appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_JOIN =
            "SELECT a.id AS a_id, a.appointment_number, a.appointment_date, a.appointment_time, a.status, " +
                    "p.id AS p_id, p.name AS p_name, p.address AS p_address, p.contact_number AS p_contact, " +
                    "d.id AS d_id, d.name AS d_name, d.specialization AS d_specialization, " +
                    "t.id AS t_id, t.name AS t_name, t.fee AS t_fee " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "JOIN dentists d ON a.dentist_id = d.id " +
                    "JOIN treatments t ON a.treatment_id = t.id ";

    private static final String FIND_BY_NUMBER_SQL = SELECT_JOIN + "WHERE a.appointment_number = ?";
    private static final String EXISTS_BY_NUMBER_SQL = "SELECT COUNT(*) FROM appointments WHERE appointment_number = ?";
    private static final String FIND_ALL_SQL = SELECT_JOIN + "ORDER BY a.appointment_date, a.appointment_time";

    @Override
    public Appointment save(Appointment appointment) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, appointment.getAppointmentNumber());
            ps.setInt(2, appointment.getPatient().getId());
            ps.setInt(3, appointment.getDentist().getId());
            ps.setInt(4, appointment.getTreatment().getId());
            ps.setDate(5, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            ps.setTime(6, java.sql.Time.valueOf(appointment.getAppointmentTime()));
            ps.setString(7, appointment.getStatus().name());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    appointment.setId(keys.getInt(1));
                }
            }
            return appointment;
        }
    }

    @Override
    public Appointment findByAppointmentNumber(String appointmentNumber) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_NUMBER_SQL)) {

            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        }
    }

    @Override
    public boolean existsByAppointmentNumber(String appointmentNumber) throws SQLException {
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(EXISTS_BY_NUMBER_SQL)) {

            ps.setString(1, appointmentNumber);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public List<Appointment> findAll() throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                appointments.add(mapRow(rs));
            }
        }
        return appointments;
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("p_id"));
        patient.setName(rs.getString("p_name"));
        patient.setAddress(rs.getString("p_address"));
        patient.setContactNumber(rs.getString("p_contact"));

        Dentist dentist = new Dentist();
        dentist.setId(rs.getInt("d_id"));
        dentist.setName(rs.getString("d_name"));
        dentist.setSpecialization(rs.getString("d_specialization"));

        Treatment treatment = new Treatment();
        treatment.setId(rs.getInt("t_id"));
        treatment.setName(rs.getString("t_name"));
        treatment.setFee(rs.getDouble("t_fee"));

        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("a_id"));
        appointment.setAppointmentNumber(rs.getString("appointment_number"));
        appointment.setPatient(patient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setStatus(AppointmentStatus.valueOf(rs.getString("status")));
        return appointment;
    }
}
