package com.ticket.transaction.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TicketCalculation {
    TicketType ticketType;
    int quantity;
    double totalCost;
    boolean discountApplied;

    public String getTicketTypeName() {
        return ticketType.getDisplayName();
    }
}
