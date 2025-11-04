package com.cinema.pricing.service;

import com.cinema.pricing.domain.TicketType;

import java.util.Map;

public interface PricingStrategy {

    double calculateCost(TicketType ticketType, int quantity, Map<TicketType, Integer> ticketCounts);

    boolean isDiscountApplied(TicketType ticketType, int quantity, Map<TicketType, Integer> ticketCounts);
}
