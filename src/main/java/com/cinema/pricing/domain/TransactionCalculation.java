package com.cinema.pricing.domain;

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
}
