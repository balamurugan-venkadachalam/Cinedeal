package com.ticket.transaction.service;

import com.ticket.transaction.domain.TicketType;

public interface TicketTypeResolver {
    TicketType resolveTicketType(int age);
}
