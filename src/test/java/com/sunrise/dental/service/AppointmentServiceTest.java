package com.sunrise.dental.service;

import com.sunrise.dental.dao.AppointmentDAO;
import com.sunrise.dental.dao.PatientDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.Treatment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DAOs are mocked so this test exercises AppointmentService's own logic
 * (appointment number generation, uniqueness retry, wiring the saved
 * patient into the appointment) without needing a real MySQL instance.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private AppointmentDAO appointmentDAO;

    @Test
    void registerAppointment_savesPatientAndReturnsPersistedAppointment() throws SQLException {
        AppointmentService service = new AppointmentService(patientDAO, appointmentDAO);

        Patient patient = new Patient("Nadeesha Silva", "12 Galle Road, Colombo", "0771234567");
        Patient savedPatient = new Patient("Nadeesha Silva", "12 Galle Road, Colombo", "0771234567");
        savedPatient.setId(1);

        Dentist dentist = new Dentist(1, "Dr. Nimal Perera", "General Dentistry");
        Treatment treatment = new Treatment(2, "Scaling & Polishing", 3500.00);

        when(patientDAO.save(patient)).thenReturn(savedPatient);
        when(appointmentDAO.existsByAppointmentNumber(anyString())).thenReturn(false);
        when(appointmentDAO.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment appointment = invocation.getArgument(0);
            appointment.setId(10);
            return appointment;
        });

        Appointment result = service.registerAppointment(
                patient, dentist, treatment, LocalDate.now().plusDays(1), LocalTime.of(10, 0));

        assertEquals(10, result.getId());
        assertNotNull(result.getAppointmentNumber());
        assertEquals(savedPatient, result.getPatient());
        assertEquals(dentist, result.getDentist());
        assertEquals(treatment, result.getTreatment());

        verify(patientDAO).save(patient);
        verify(appointmentDAO).save(any(Appointment.class));
    }

    @Test
    void registerAppointment_regeneratesNumberWhenFirstOneIsAlreadyTaken() throws SQLException {
        AppointmentService service = new AppointmentService(patientDAO, appointmentDAO);

        Patient savedPatient = new Patient("Kamal Jayasuriya", "45 Kandy Road", "0719876543");
        savedPatient.setId(2);
        Dentist dentist = new Dentist(2, "Dr. Amaya Silva", "Orthodontics");
        Treatment treatment = new Treatment(1, "Consultation Only", 0.00);

        when(patientDAO.save(any(Patient.class))).thenReturn(savedPatient);
        // First generated number is reported as already used, second is free.
        when(appointmentDAO.existsByAppointmentNumber(anyString())).thenReturn(true, false);
        when(appointmentDAO.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.registerAppointment(
                new Patient("Kamal Jayasuriya", "45 Kandy Road", "0719876543"),
                dentist, treatment, LocalDate.now(), LocalTime.of(14, 30));

        verify(appointmentDAO, org.mockito.Mockito.times(2)).existsByAppointmentNumber(anyString());
    }
}
