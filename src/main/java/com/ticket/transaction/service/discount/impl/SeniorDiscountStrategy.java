package com.ticket.transaction.service.discount.impl;

import com.ticket.transaction.config.PricingConfiguration;
import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.service.discount.DiscountContext;
import com.ticket.transaction.service.discount.DiscountResult;
import com.ticket.transaction.service.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeniorDiscountStrategy implements DiscountStrategy {

    private final PricingConfiguration config;

    @Override
    public DiscountResult calculateDiscount(DiscountContext context) {
        double discount = context.getBaseTotalCost() * config.getSeniorDiscountRate();
        log.debug("Senior discount for {}: {} tickets @ {}% off = ${}",
                context.getTicketType(),
                context.getQuantity(),
                config.getSeniorDiscountRate() * 100,
                discount);
        return DiscountResult.applied(getDiscountType(), getDisplayName(), discount, config.getSeniorDiscountRate());
    }

    @Override
    public boolean isApplicable(DiscountContext context) {
        return context.getTicketType() == TicketType.SENIOR;
    }

    @Override
    public int getPriority() {
        return 150;
    }

    @Override
    public String getDiscountType() {
        return "SENIOR_DISCOUNT";
    }

    @Override
    public String getDisplayName() {
        return "Senior Citizen Discount";
    }
}
