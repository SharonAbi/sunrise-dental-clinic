package com.sunrise.dental.service.billing;

public class InsuranceBillingStrategy implements BillingStrategy {

    private static final double TREATMENT_DISCOUNT_RATE = 0.20;

    @Override
    public double calculateDiscount(double consultationFee, double treatmentFee) {
        return treatmentFee * TREATMENT_DISCOUNT_RATE;
    }

    @Override
    public double calculateTotal(double consultationFee, double treatmentFee) {
        return consultationFee + treatmentFee - calculateDiscount(consultationFee, treatmentFee);
    }

    @Override
    public String getDescription() {
        return "Insurance billing (20% discount on treatment fee)";
    }
}
