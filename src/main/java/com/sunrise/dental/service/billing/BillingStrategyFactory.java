package com.sunrise.dental.service.billing;

/**
 * Factory pattern: selects the correct BillingStrategy so callers never
 * instantiate a concrete strategy class directly.
 */
public final class BillingStrategyFactory {

    private BillingStrategyFactory() {
    }

    public static BillingStrategy getStrategy(boolean hasInsurance) {
        return hasInsurance ? new InsuranceBillingStrategy() : new StandardBillingStrategy();
    }
}
