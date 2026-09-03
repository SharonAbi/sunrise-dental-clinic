package com.sunrise.dental.servlet;

import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.service.AppointmentService;
import com.sunrise.dental.service.BillService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/bill")
public class BillServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();
    private final BillService billService = new BillService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String number = req.getParameter("number");
        boolean hasInsurance = "on".equals(req.getParameter("insurance"));

        if (number == null || number.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/search-appointment");
            return;
        }

        try {
            Appointment appointment = appointmentService.findAppointment(number.trim());
            if (appointment == null) {
                req.setAttribute("errorMessage", "No appointment found for number: " + number);
                req.getRequestDispatcher("/searchAppointment.jsp").forward(req, resp);
                return;
            }

            Bill bill = billService.generateBill(appointment, hasInsurance);
            req.setAttribute("bill", bill);
            req.getRequestDispatcher("/bill.jsp").forward(req, resp);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Could not generate the bill. Please try again.");
            req.getRequestDispatcher("/searchAppointment.jsp").forward(req, resp);
        }
    }
}
