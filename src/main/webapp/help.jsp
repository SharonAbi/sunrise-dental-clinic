<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Help - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <nav class="topbar">
        <span>Sunrise Dental Clinic</span>
        <a href="${pageContext.request.contextPath}/dashboard">Back to Menu</a>
    </nav>

    <h2>Help - How to Use the System</h2>
    <ol>
        <li><strong>Login:</strong> Enter your staff username and password on the login screen.</li>
        <li><strong>Register New Appointment:</strong> From the main menu choose "Register New Appointment",
            fill in the patient's details, select a dentist and treatment type, and pick a date/time.
            The system generates a unique appointment number automatically.</li>
        <li><strong>Display Appointment Details:</strong> Choose "Display Appointment Details" and enter
            the appointment number to view the patient and appointment information.</li>
        <li><strong>Calculate &amp; Print Bill:</strong> From the appointment details page, tick
            "insurance" if applicable, then click "Calculate &amp; Print Bill" to view and print the
            receipt.</li>
        <li><strong>Exit:</strong> Click "Logout" from the menu to safely end your session.</li>
    </ol>
</div>
</body>
</html>
