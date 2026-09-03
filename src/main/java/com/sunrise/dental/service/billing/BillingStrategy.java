package com.sunrise.dental.service.billing;

/**
 * Strategy pattern: encapsulates how a bill total is derived from the
 * consultation and treatment fees, so new billing rules (e.g. corporate
 * discounts, promotions) can be added as new strategies without touching
 * BillService.
 */
public interface BillingStrategy {

    double calculateDiscount(double consultationFee, double treatmentFee);

    double calculateTotal(double consultationFee, double treatmentFee);

    String getDescription();
}
