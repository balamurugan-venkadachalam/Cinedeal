package com.cinema.pricing.service;

import com.cinema.pricing.domain.TicketType;

public interface TicketPriceProvider {

    double getBasePrice(TicketType ticketType);
}
