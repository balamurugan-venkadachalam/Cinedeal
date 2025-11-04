package com.cinema.pricing.service.discount;

/**
 * Strategy interface for different discount types.
 * Each implementation represents a specific discount rule.
 */
public interface DiscountStrategy {

    DiscountResult calculateDiscount(DiscountContext context);

    boolean isApplicable(DiscountContext context);

    int getPriority();

    String getDiscountType();


    String getDisplayName();
}
