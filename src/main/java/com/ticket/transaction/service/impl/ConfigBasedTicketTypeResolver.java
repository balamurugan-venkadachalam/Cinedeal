package com.ticket.transaction.service.impl;

import com.ticket.transaction.config.AgeRange;
import com.ticket.transaction.config.PricingConfiguration;
import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.service.TicketTypeResolver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConfigBasedTicketTypeResolver implements TicketTypeResolver {

    private final PricingConfiguration config;

    @PostConstruct
    public void init() {
        if (config.getAgeRanges().isEmpty()) {
            initializeDefaultAgeRanges();
        }
    }

    @Override
    public TicketType resolveTicketType(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }

        return Stream.of(TicketType.values())
                .filter(ticketType -> isAgeInRange(ticketType, age))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No ticket type found for age: " + age));
    }

    private boolean isAgeInRange(TicketType ticketType, int age) {
        String typeName = ticketType.name().toLowerCase();
        return Optional.ofNullable(config.getAgeRanges().get(typeName))
                .map(range -> range.contains(age))
                .orElse(false);
    }

    private void initializeDefaultAgeRanges() {
        Map<String, AgeRange> ageRanges = config.getAgeRanges();

        ageRanges.put(TicketType.CHILDREN.name().toLowerCase(), createAgeRange(0, 10));
        ageRanges.put(TicketType.TEEN.name().toLowerCase(), createAgeRange(11, 17));
        ageRanges.put(TicketType.ADULT.name().toLowerCase(), createAgeRange(18, 64));
        ageRanges.put(TicketType.SENIOR.name().toLowerCase(), createAgeRange(65, Integer.MAX_VALUE));
    }

    private AgeRange createAgeRange(int min, int max) {
        AgeRange range = new AgeRange();
        range.setMin(min);
        range.setMax(max);
        return range;
    }

}
