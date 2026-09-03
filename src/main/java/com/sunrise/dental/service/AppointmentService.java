package com.sunrise.dental.service;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.DAOFactory;
import com.sunrise.dental.dao.PatientDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.AppointmentStatus;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.util.AppointmentNumberGenerator;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentService {

    private final PatientDAO patientDAO;
    private final AppointmentDAO appointmentDAO;

    public AppointmentService() {
        this(DAOFactory.getPatientDAO(), DAOFactory.getAppointmentDAO());
    }

    /** Constructor injection - lets unit tests supply mock DAOs instead of hitting MySQL. */
    public AppointmentService(PatientDAO patientDAO, AppointmentDAO appointmentDAO) {
        this.patientDAO = patientDAO;
        this.appointmentDAO = appointmentDAO;
    }

    public Appointment registerAppointment(Patient patient, Dentist dentist, Treatment treatment,
                                            LocalDate date, LocalTime time) throws SQLException {
        Patient savedPatient = patientDAO.save(patient);

        Appointment appointment = new Appointment();
        appointment.setAppointmentNumber(generateUniqueAppointmentNumber());
        appointment.setPatient(savedPatient);
        appointment.setDentist(dentist);
        appointment.setTreatment(treatment);
        appointment.setAppointmentDate(date);
        appointment.setAppointmentTime(time);
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        return appointmentDAO.save(appointment);
    }

    public Appointment findAppointment(String appointmentNumber) throws SQLException {
        return appointmentDAO.findByAppointmentNumber(appointmentNumber);
    }

    private String generateUniqueAppointmentNumber() throws SQLException {
        String number;
        do {
            number = AppointmentNumberGenerator.generate();
        } while (appointmentDAO.existsByAppointmentNumber(number));
        return number;
    }
}
