package com.cinema.pricing.service.discount;

import lombok.Builder;

@Builder
public record DiscountResult(String discountType, String displayName, double discountAmount, double discountRate,
                             boolean applied, String reason) {
    public static DiscountResult noDiscount(String reason) {
        return DiscountResult.builder()
                .applied(false)
                .discountAmount(0.0)
                .discountRate(0.0)
                .reason(reason)
                .build();
    }

    public static DiscountResult applied(String type, String name,
                                         double amount, double rate) {
        return DiscountResult.builder()
                .discountType(type)
                .displayName(name)
                .discountAmount(amount)
                .discountRate(rate)
                .applied(true)
                .reason("Discount applied successfully")
                .build();
    }
}
