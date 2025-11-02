package com.ticket.transaction.domain;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class TransactionCalculation {
    Long transactionId;

    @Singular
    List<TicketCalculation> ticketCalculations;

    double totalCost;

    public int getTicketTypeCount() {
        return ticketCalculations.size();
    }

    public int getTotalTicketQuantity() {
        return ticketCalculations.stream()
                .mapToInt(TicketCalculation::getQuantity)
                .sum();
    }
}
