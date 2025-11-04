package com.cinema.pricing.service.discount;

import com.cinema.pricing.config.PricingConfiguration;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DiscountEngine {

    private final List<DiscountStrategy> strategies;

    private List<DiscountStrategy> sortedStrategies;

    public DiscountEngine(List<DiscountStrategy> strategies) {
        this.strategies = strategies;
    }

    @PostConstruct
    public void init() {
        // Sort by priority
        sortedStrategies = new ArrayList<>(strategies);
        sortedStrategies.sort(Comparator.comparingInt(DiscountStrategy::getPriority).reversed());

        log.info("Initialized {} discount strategies: {}",
                sortedStrategies.size(),
                sortedStrategies.stream()
                        .map(DiscountStrategy::getDisplayName)
                        .collect(Collectors.joining(", ")));
    }

    public DiscountSummary applyDiscounts(DiscountContext context) {
        List<DiscountResult> results = sortedStrategies.stream()
                .filter(strategy -> strategy.isApplicable(context))
                .map(strategy -> {
                            DiscountResult result = strategy.calculateDiscount(context);
                            if (result.isApplied()) {
                                log.debug("Discount applied: {} - {}", result.getDisplayName(), result.getDiscountAmount());
                            }
                            return result;
                        }

                ).filter(DiscountResult::isApplied)
                .collect(Collectors.toList());

        var totalDiscount = results.stream()
                .mapToDouble(DiscountResult::getDiscountAmount)
                .sum();

        if (!results.isEmpty()) {
            log.info("Applied {} discounts totaling ${} for {}",
                    results.size(), totalDiscount, context.getTicketType());
        }

        return DiscountSummary.builder()
                .appliedDiscounts(results)
                .totalDiscountAmount(totalDiscount)
                .build();

    }

}
