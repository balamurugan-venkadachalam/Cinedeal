package com.ticket.transaction.service;

import com.ticket.transaction.domain.TicketType;

public interface TicketPriceProvider {
    double getBasePrice(TicketType ticketType) ;
}
