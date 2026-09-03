package com.sunrise.dental.servlet;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.service.AppointmentService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/search-appointment")
public class SearchAppointmentServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = req.getParameter("number");
        if (number == null || number.trim().isEmpty()) {
            req.getRequestDispatcher("/searchAppointment.jsp").forward(req, resp);
            return;
        }

        try {
            Appointment appointment = appointmentService.findAppointment(number.trim());
            if (appointment == null) {
                req.setAttribute("errorMessage", "No appointment found for number: " + number);
                req.getRequestDispatcher("/searchAppointment.jsp").forward(req, resp);
            } else {
                req.setAttribute("appointment", appointment);
                req.getRequestDispatcher("/appointmentDetails.jsp").forward(req, resp);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "System error while searching. Please try again.");
            req.getRequestDispatcher("/searchAppointment.jsp").forward(req, resp);
        }
    }
}
