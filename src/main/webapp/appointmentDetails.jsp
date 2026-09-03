<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Appointment Details - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <nav class="topbar">
        <span>Sunrise Dental Clinic</span>
        <a href="${pageContext.request.contextPath}/dashboard">Back to Menu</a>
    </nav>

    <h2>Appointment Details</h2>

    <table class="details-table">
        <tr><th>Appointment Number</th><td>${appointment.appointmentNumber}</td></tr>
        <tr><th>Patient Name</th><td>${appointment.patient.name}</td></tr>
        <tr><th>Address</th><td>${appointment.patient.address}</td></tr>
        <tr><th>Contact Number</th><td>${appointment.patient.contactNumber}</td></tr>
        <tr><th>Dentist</th><td>${appointment.dentist.name}</td></tr>
        <tr><th>Treatment Type</th><td>${appointment.treatment.name}</td></tr>
        <tr><th>Date</th><td>${appointment.appointmentDate}</td></tr>
        <tr><th>Time</th><td>${appointment.appointmentTime}</td></tr>
        <tr><th>Status</th><td>${appointment.status}</td></tr>
    </table>

    <form method="get" action="${pageContext.request.contextPath}/bill">
        <input type="hidden" name="number" value="${appointment.appointmentNumber}">
        <label><input type="checkbox" name="insurance"> Patient has insurance (20% treatment discount)</label>
        <button type="submit">Calculate &amp; Print Bill</button>
    </form>
</div>
</body>
</html>
