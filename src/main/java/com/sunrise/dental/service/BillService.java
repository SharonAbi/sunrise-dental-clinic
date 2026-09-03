package com.sunrise.dental.service;

import com.sunrise.dental.dao.BillDAO;
import com.sunrise.dental.dao.DAOFactory;
import com.sunrise.dental.model.Appointment;
import com.sunrise.dental.model.Bill;
import com.sunrise.dental.service.billing.BillingStrategy;
import com.sunrise.dental.service.billing.BillingStrategyFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class BillService {

    /** Flat consultation charge applied to every appointment, regardless of treatment type. */
    private static final double CONSULTATION_FEE = 1500.00;

    private final BillDAO billDAO;

    public BillService() {
        this(DAOFactory.getBillDAO());
    }

    public BillService(BillDAO billDAO) {
        this.billDAO = billDAO;
    }

    /**
     * Returns the existing bill for this appointment if one was already
     * generated, otherwise calculates and persists a new one. This keeps
     * billing idempotent - reprinting a receipt does not create duplicate
     * bill records.
     */
    public Bill generateBill(Appointment appointment, boolean hasInsurance) throws SQLException {
        Bill existing = billDAO.findByAppointmentId(appointment.getId());
        if (existing != null) {
            existing.setAppointment(appointment);
            return existing;
        }

        BillingStrategy strategy = BillingStrategyFactory.getStrategy(hasInsurance);
        double treatmentFee = appointment.getTreatment().getFee();
        double discount = strategy.calculateDiscount(CONSULTATION_FEE, treatmentFee);
        double total = strategy.calculateTotal(CONSULTATION_FEE, treatmentFee);

        Bill bill = new Bill();
        bill.setAppointment(appointment);
        bill.setConsultationFee(CONSULTATION_FEE);
        bill.setTreatmentFee(treatmentFee);
        bill.setDiscountAmount(discount);
        bill.setTotalAmount(total);
        bill.setInsuranceApplied(hasInsurance);
        bill.setGeneratedAt(LocalDateTime.now());

        Bill saved = billDAO.save(bill);
        saved.setAppointment(appointment);
        return saved;
    }
}
