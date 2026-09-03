package com.sunrise.dental.dao;

import com.sunrise.dental.model.Appointment;

import java.sql.SQLException;
import java.util.List;

public interface AppointmentDAO {

    Appointment save(Appointment appointment) throws SQLException;

    Appointment findByAppointmentNumber(String appointmentNumber) throws SQLException;

    boolean existsByAppointmentNumber(String appointmentNumber) throws SQLException;

    List<Appointment> findAll() throws SQLException;
}
