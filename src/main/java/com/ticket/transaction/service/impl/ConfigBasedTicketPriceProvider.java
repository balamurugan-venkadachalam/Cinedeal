package com.ticket.transaction.service.impl;

import com.ticket.transaction.config.PricingConfiguration;
import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.service.TicketPriceProvider;
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
