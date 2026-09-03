<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bill / Receipt - Sunrise Dental Clinic</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <nav class="topbar no-print">
        <span>Sunrise Dental Clinic</span>
        <a href="${pageContext.request.contextPath}/dashboard">Back to Menu</a>
    </nav>

    <div class="receipt">
        <h2>Sunrise Dental Clinic - Receipt</h2>
        <p>Appointment No: ${bill.appointment.appointmentNumber}</p>
        <p>Patient: ${bill.appointment.patient.name}</p>
        <p>Dentist: ${bill.appointment.dentist.name}</p>
        <p>Treatment: ${bill.appointment.treatment.name}</p>

        <table class="details-table">
            <tr><th>Consultation Fee</th><td>Rs. ${bill.consultationFee}</td></tr>
            <tr><th>Treatment Fee</th><td>Rs. ${bill.treatmentFee}</td></tr>
            <tr><th>Discount</th><td>Rs. ${bill.discountAmount}</td></tr>
            <tr><th>Total Amount</th><td><strong>Rs. ${bill.totalAmount}</strong></td></tr>
        </table>

        <p>Generated: ${bill.generatedAt}</p>
    </div>

    <button class="no-print" onclick="window.print()">Print Bill</button>
</div>
</body>
</html>
