package com.cinema.pricing.service.discount;

import lombok.Builder;

import java.util.List;

@Builder
public record DiscountSummary(List<DiscountResult> appliedDiscounts, double totalDiscountAmount) {

    public boolean hasDiscounts() {
        return appliedDiscounts != null && !appliedDiscounts.isEmpty();

    }
}
