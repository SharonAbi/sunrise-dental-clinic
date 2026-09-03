<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <nav class="topbar">
        <span>Sunrise Dental Clinic</span>
        <span>Welcome, ${sessionScope.user.fullName}
            | <a href="${pageContext.request.contextPath}/logout">Logout</a></span>
    </nav>

    <h2>Main Menu</h2>
    <div class="menu-grid">
        <a class="menu-card" href="${pageContext.request.contextPath}/register-appointment">
            Register New Appointment</a>
        <a class="menu-card" href="${pageContext.request.contextPath}/search-appointment">
            Display Appointment Details</a>
        <a class="menu-card" href="${pageContext.request.contextPath}/search-appointment">
            Calculate &amp; Print Bill</a>
        <a class="menu-card" href="${pageContext.request.contextPath}/help">
            Help</a>
        <a class="menu-card" href="${pageContext.request.contextPath}/logout">
            Exit / Logout</a>
    </div>
</div>
</body>
</html>
