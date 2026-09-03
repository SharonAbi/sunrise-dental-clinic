<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Appointment - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <nav class="topbar">
        <span>Sunrise Dental Clinic</span>
        <a href="${pageContext.request.contextPath}/dashboard">Back to Menu</a>
    </nav>

    <h2>Display Appointment Details</h2>

    <c:if test="${not empty errorMessage}">
        <p class="error">${errorMessage}</p>
    </c:if>

    <form method="get" action="${pageContext.request.contextPath}/search-appointment">
        <label for="number">Appointment Number</label>
        <input type="text" id="number" name="number" placeholder="APT-20260905-1234" required>
        <button type="submit">Search</button>
    </form>
</div>
</body>
</html>
