package com.cinema.pricing.service.discount;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DiscountSummary {
    DiscountMode mode;
    List<DiscountResult> appliedDiscounts;
    double totalDiscountAmount;

    public boolean hasDiscounts() {
        return appliedDiscounts != null && !appliedDiscounts.isEmpty();
    }


}
