package com.cinema.pricing.service;

import com.cinema.pricing.domain.TicketType;

public interface TicketTypeResolver {
    TicketType resolveTicketType(int age);
}
