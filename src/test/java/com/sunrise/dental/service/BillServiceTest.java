package com.sunrise.dental.service;

import com.sunrise.dental.dao.BillDAO;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.model.Treatment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BillServiceTest {

    @Mock
    private BillDAO billDAO;

    private Appointment appointmentWithTreatmentFee(double fee) {
        Treatment treatment = new Treatment(1, "Scaling & Polishing", fee);
        Appointment appointment = new Appointment();
        appointment.setId(5);
        appointment.setTreatment(treatment);
        return appointment;
    }

    @Test
    void generateBill_standardPatient_chargesConsultationPlusTreatmentWithNoDiscount() throws SQLException {
        when(billDAO.findByAppointmentId(5)).thenReturn(null);
        when(billDAO.save(any(Bill.class))).thenAnswer(inv -> inv.getArgument(0));

        BillService billService = new BillService(billDAO);
        Bill bill = billService.generateBill(appointmentWithTreatmentFee(3500.00), false);

        assertEquals(1500.00, bill.getConsultationFee());
        assertEquals(3500.00, bill.getTreatmentFee());
        assertEquals(0.0, bill.getDiscountAmount());
        assertEquals(5000.00, bill.getTotalAmount());
        assertFalse(bill.isInsuranceApplied());
    }

    @Test
    void generateBill_insuredPatient_appliesTwentyPercentTreatmentDiscount() throws SQLException {
        when(billDAO.findByAppointmentId(5)).thenReturn(null);
        when(billDAO.save(any(Bill.class))).thenAnswer(inv -> inv.getArgument(0));

        BillService billService = new BillService(billDAO);
        Bill bill = billService.generateBill(appointmentWithTreatmentFee(3500.00), true);

        assertEquals(700.00, bill.getDiscountAmount());
        assertEquals(4300.00, bill.getTotalAmount());
        assertTrue(bill.isInsuranceApplied());
    }

    @Test
    void generateBill_returnsExistingBill_whenAlreadyGeneratedForAppointment() throws SQLException {
        Bill existingBill = new Bill();
        existingBill.setId(99);
        existingBill.setTotalAmount(5000.00);
        when(billDAO.findByAppointmentId(5)).thenReturn(existingBill);

        BillService billService = new BillService(billDAO);
        Appointment appointment = appointmentWithTreatmentFee(3500.00);
        Bill bill = billService.generateBill(appointment, false);

        assertEquals(99, bill.getId());
        assertEquals(appointment, bill.getAppointment());
        verify(billDAO, never()).save(any(Bill.class));
    }
}
