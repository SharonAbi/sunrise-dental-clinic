package com.sunrise.dental.servlet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.service.AppointmentService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Small JSON web service exposing appointment lookups over HTTP, e.g.
 * GET /api/appointments?number=APT-20260905-1234
 *
 * This is the "distributed application with web services" element of the
 * brief: any external client (a mobile app, another system, curl/Postman)
 * can query appointment data without going through the JSP screens.
 */
@WebServlet("/api/appointments")
public class AppointmentApiServlet extends HttpServlet {

    private final AppointmentService appointmentService = new AppointmentService();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalTime.class,
                    (JsonSerializer<LocalTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String number = req.getParameter("number");
        if (number == null || number.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Missing required query parameter: number\"}");
            return;
        }

        try {
            Appointment appointment = appointmentService.findAppointment(number.trim());
            if (appointment == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Appointment not found\"}");
                return;
            }
            out.write(gson.toJson(appointment));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Internal server error\"}");
        }
    }
}
