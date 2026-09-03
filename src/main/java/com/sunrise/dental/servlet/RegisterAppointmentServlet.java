package com.sunrise.dental.servlet;

import com.sunrise.dental.dao.DAOFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Dentist;
import com.sunrise.dental.model.Patient;
import com.sunrise.dental.model.Treatment;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet("/register-appointment")
public class RegisterAppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        loadFormReferenceData(req);
        req.getRequestDispatcher("/registerAppointment.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String contact = req.getParameter("contact");
        String dentistId = req.getParameter("dentistId");
        String treatmentId = req.getParameter("treatmentId");
        String date = req.getParameter("date");
        String time = req.getParameter("time");

        List<String> errors = validate(name, address, contact, date, time);

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            loadFormReferenceData(req);
            req.getRequestDispatcher("/registerAppointment.jsp").forward(req, resp);
            return;
        }

        try {
            Dentist dentist = DAOFactory.getDentistDAO().findById(Integer.parseInt(dentistId));
            Treatment treatment = DAOFactory.getTreatmentDAO().findById(Integer.parseInt(treatmentId));
            Patient patient = new Patient(name.trim(), address.trim(), contact.trim());

            Appointment appointment = appointmentService.registerAppointment(
                    patient, dentist, treatment, LocalDate.parse(date), LocalTime.parse(time));

            req.setAttribute("appointment", appointment);
            req.getRequestDispatcher("/appointmentDetails.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errors", Collections.singletonList("Could not save the appointment. Please try again."));
            loadFormReferenceData(req);
            req.getRequestDispatcher("/registerAppointment.jsp").forward(req, resp);
        }
    }

    private List<String> validate(String name, String address, String contact, String date, String time) {
        List<String> errors = new ArrayList<>();
        if (!ValidationUtil.isValidName(name)) {
            errors.add("Please enter a valid patient name (letters and spaces only).");
        }
        if (!ValidationUtil.isNotEmpty(address)) {
            errors.add("Address is required.");
        }
        if (!ValidationUtil.isValidContactNumber(contact)) {
            errors.add("Contact number must be 10 digits starting with 0 (e.g. 0771234567).");
        }
        if (!ValidationUtil.isValidDate(date)) {
            errors.add("Please select a valid appointment date.");
        } else if (!ValidationUtil.isFutureOrTodayDate(LocalDate.parse(date))) {
            errors.add("Appointment date cannot be in the past.");
        }
        if (!ValidationUtil.isValidTime(time)) {
            errors.add("Please select a valid appointment time.");
        }
        return errors;
    }

    private void loadFormReferenceData(HttpServletRequest req) throws ServletException {
        try {
            req.setAttribute("dentists", DAOFactory.getDentistDAO().findAll());
            req.setAttribute("treatments", DAOFactory.getTreatmentDAO().findAll());
        } catch (SQLException e) {
            throw new ServletException("Failed to load dentists/treatments", e);
        }
    }
}
