<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Register Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <nav class="topbar">
        <span>Sunrise Dental Clinic</span>
        <a href="${pageContext.request.contextPath}/dashboard">Back to Menu</a>
    </nav>

    <h2>Register New Appointment</h2>

    <c:if test="${not empty errors}">
        <ul class="error">
            <c:forEach var="e" items="${errors}"><li>${e}</li></c:forEach>
        </ul>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/register-appointment">
        <label for="name">Patient Name</label>
        <input type="text" id="name" name="name" required>

        <label for="address">Address</label>
        <input type="text" id="address" name="address" required>

        <label for="contact">Contact Number</label>
        <input type="text" id="contact" name="contact" placeholder="0771234567" required>

        <label for="dentistId">Dentist</label>
        <select id="dentistId" name="dentistId" required>
            <c:forEach var="d" items="${dentists}">
                <option value="${d.id}">${d.name} (${d.specialization})</option>
            </c:forEach>
        </select>

        <label for="treatmentId">Treatment Type</label>
        <select id="treatmentId" name="treatmentId" required>
            <c:forEach var="t" items="${treatments}">
                <option value="${t.id}">${t.name} - Rs. ${t.fee}</option>
            </c:forEach>
        </select>

        <label for="date">Appointment Date</label>
        <input type="date" id="date" name="date" required>

        <label for="time">Appointment Time</label>
        <input type="time" id="time" name="time" required>

        <button type="submit">Register Appointment</button>
    </form>
</div>
</body>
</html>
