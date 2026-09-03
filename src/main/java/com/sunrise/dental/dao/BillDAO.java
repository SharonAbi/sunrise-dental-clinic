package com.sunrise.dental.dao;

import com.sunrise.dental.model.Bill;

import java.sql.SQLException;

public interface BillDAO {

    Bill save(Bill bill) throws SQLException;

    Bill findByAppointmentId(int appointmentId) throws SQLException;
}
