package com.cinema.pricing.service.impl;

import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.service.TicketPriceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfigBasedTicketPriceProvider implements TicketPriceProvider {

    private final PricingConfiguration config;

    @Override
    public double getBasePrice(TicketType ticketType) {
        return switch (ticketType) {
            case ADULT -> config.getAdultBasePrice();
            case SENIOR -> config.getSeniorBasePrice();
            case TEEN -> config.getTeenBasePrice();
            case CHILDREN -> config.getChildrenBasePrice();
        };
    }
}
