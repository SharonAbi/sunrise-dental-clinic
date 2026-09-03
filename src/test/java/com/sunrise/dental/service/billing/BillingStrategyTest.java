package com.sunrise.dental.service.billing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BillingStrategyTest {

    @Test
    void standardStrategy_appliesNoDiscount() {
        BillingStrategy strategy = new StandardBillingStrategy();

        assertEquals(0.0, strategy.calculateDiscount(1500, 5000));
        assertEquals(6500.0, strategy.calculateTotal(1500, 5000));
    }

    @Test
    void insuranceStrategy_appliesTwentyPercentDiscountOnTreatmentFeeOnly() {
        BillingStrategy strategy = new InsuranceBillingStrategy();

        assertEquals(1000.0, strategy.calculateDiscount(1500, 5000));
        assertEquals(5500.0, strategy.calculateTotal(1500, 5000));
    }

    @Test
    void factory_returnsInsuranceStrategyWhenInsuranceFlagIsTrue() {
        assertTrue(BillingStrategyFactory.getStrategy(true) instanceof InsuranceBillingStrategy);
    }

    @Test
    void factory_returnsStandardStrategyWhenInsuranceFlagIsFalse() {
        assertTrue(BillingStrategyFactory.getStrategy(false) instanceof StandardBillingStrategy);
    }
}
