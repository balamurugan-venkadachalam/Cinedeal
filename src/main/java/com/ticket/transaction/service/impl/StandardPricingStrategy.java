package com.ticket.transaction.service.impl;

import com.ticket.transaction.config.BulkDiscountRule;
import com.ticket.transaction.config.PricingConfiguration;

import com.ticket.transaction.domain.TicketType;

import com.ticket.transaction.service.PricingStrategy;
import com.ticket.transaction.service.TicketPriceProvider;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class StandardPricingStrategy implements PricingStrategy {

    private final PricingConfiguration pricingConfiguration;
    private final TicketPriceProvider priceProvider;

    @PostConstruct
    public void init() {
        if (pricingConfiguration.getBulkDiscounts().isEmpty()) {
            log.info("No bulk discounts configured, using default children bulk discount (3+ tickets @ 25% off)");
            BulkDiscountRule defaultRule = new BulkDiscountRule();
            defaultRule.setTicketType("children");
            defaultRule.setQuantity(3);
            defaultRule.setDiscountRate(0.25);
            pricingConfiguration.getBulkDiscounts().add(defaultRule);
        }
    }

    @Override
    public double calculateCost(TicketType ticketType, int quantity, Map<TicketType, Integer> ticketCounts) {
        double basePrice = priceProvider.getBasePrice(ticketType);
        double baseTotalCost = basePrice * quantity;

        double totalCost = findApplicableBulkDiscount(ticketType, quantity)
                .map(rule -> {
                    log.debug("Applied bulk discount for {}: {} tickets @ {}% off",
                            ticketType, quantity, rule.getDiscountRate() * 100);
                    return baseTotalCost * (1 - rule.getDiscountRate());
                })
                .orElse(baseTotalCost);

        // Round to 2 decimal places
        return Math.round(totalCost * 100.0) / 100.0;
    }

    @Override
    public boolean isDiscountApplied(TicketType ticketType, int quantity, Map<TicketType, Integer> ticketCounts) {
        return true;
    }

    private Optional<BulkDiscountRule> findApplicableBulkDiscount(TicketType ticketType, int quantity) {
        String ticketTypeName = ticketType.name().toLowerCase();

        return pricingConfiguration.getBulkDiscounts().stream()
                .filter(rule -> rule.getTicketType().equalsIgnoreCase(ticketTypeName))
                .filter(rule -> quantity >= rule.getQuantity())
                .findFirst();
    }
}
