package com.sunrise.dental.service.billing;

public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public double calculateDiscount(double consultationFee, double treatmentFee) {
        return 0.0;
    }

    @Override
    public double calculateTotal(double consultationFee, double treatmentFee) {
        return consultationFee + treatmentFee;
    }

    @Override
    public String getDescription() {
        return "Standard billing (no discount)";
    }
}
