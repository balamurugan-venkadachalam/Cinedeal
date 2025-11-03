package com.ticket.transaction.service.impl;

import com.ticket.transaction.config.BulkDiscountConfig;
import com.ticket.transaction.config.PricingConfiguration;
import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.service.PricingStrategy;
import com.ticket.transaction.service.TicketPriceProvider;
import com.ticket.transaction.service.discount.DiscountContext;
import com.ticket.transaction.service.discount.DiscountEngine;
import com.ticket.transaction.service.discount.DiscountSummary;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StandardPricingStrategy implements PricingStrategy {

    private final PricingConfiguration pricingConfiguration;
    private final TicketPriceProvider priceProvider;
    private final DiscountEngine discountEngine;

    @PostConstruct
    public void init() {
        if (pricingConfiguration.getBulkDiscounts().isEmpty()) {
            log.info("No bulk discounts configured, using default children bulk discount (3+ tickets @ 25% off)");
            BulkDiscountConfig defaultRule = new BulkDiscountConfig();
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

        DiscountContext context = DiscountContext.builder()
                .ticketType(ticketType)
                .quantity(quantity)
                .basePrice(basePrice)
                .baseTotalCost(baseTotalCost)
                .allTicketCounts(ticketCounts)
                .transactionTime(LocalDateTime.now())
                .build();

        DiscountSummary summary = discountEngine.applyDiscounts(context);

        var finalCost = baseTotalCost - summary.getTotalDiscountAmount();
        finalCost = finalCost < 0 ? 0.0 : finalCost;

        // Round to 2 decimal places
        return Math.round(finalCost * 100.0) / 100.0;
    }

    @Override
    public boolean isDiscountApplied(TicketType ticketType, int quantity, Map<TicketType, Integer> ticketCounts) {
        DiscountContext context = DiscountContext.builder()
                .ticketType(ticketType)
                .quantity(quantity)
                .allTicketCounts(ticketCounts)
                .transactionTime(LocalDateTime.now())
                .build();
        return discountEngine.applyDiscounts(context).hasDiscounts();
    }


}
