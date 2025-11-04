package com.cinema.pricing.service.discount;

public interface DiscountStrategy {

    DiscountResult calculateDiscount(DiscountContext context);

    boolean isApplicable(DiscountContext context);

    int getPriority();

    String getDiscountType();


    String getDisplayName();
}
