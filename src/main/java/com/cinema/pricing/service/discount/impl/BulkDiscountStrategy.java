package com.cinema.pricing.service.discount.impl;

import com.cinema.pricing.config.BulkDiscountConfig;
import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.service.discount.DiscountContext;
import com.cinema.pricing.service.discount.DiscountResult;
import com.cinema.pricing.service.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class BulkDiscountStrategy implements DiscountStrategy {

    private final PricingConfiguration config;

    @Override
    public DiscountResult calculateDiscount(DiscountContext context) {
        return findApplicableBulkDiscount(context.getTicketType(), context.getQuantity())
                .map(rule -> {
                    var discount = context.getBaseTotalCost() * rule.getDiscountRate();

                    log.debug("Bulk discount for {}: {} tickets @ {}% off = ${}",
                            context.getTicketType(),
                            context.getQuantity(),
                            rule.getDiscountRate() * 100,
                            discount);

                    return DiscountResult.applied(
                            getDiscountType(),
                            getDisplayName(),
                            discount,
                            rule.getDiscountRate()
                    );
                }).orElse(DiscountResult.noDiscount("No applicable bulk discount found for " + context.getTicketType()));
    }

    @Override
    public boolean isApplicable(DiscountContext context) {
        return findApplicableBulkDiscount(context.getTicketType(), context.getQuantity()).isPresent();
    }

    @Override
    public int getPriority() {
        return 100; // configurable is better
    }

    @Override
    public String getDiscountType() {
        return "BULK DISCOUNT";
    }

    @Override
    public String getDisplayName() {
        return "Bulk Purchase Discount";
    }

    private Optional<BulkDiscountConfig> findApplicableBulkDiscount(TicketType ticketType, int quantity) {
        return config.getBulkDiscounts().stream()
                .filter(rule -> rule.getTicketType().equalsIgnoreCase(ticketType.name()))
                .filter(rule -> quantity >= rule.getQuantity())
                .findFirst();
    }
}
