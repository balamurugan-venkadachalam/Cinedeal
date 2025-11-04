package com.cinema.pricing.mapper;

import com.cinema.pricing.domain.TicketCalculation;
import com.cinema.pricing.domain.TransactionCalculation;
import com.cinema.pricing.model.Ticket;
import com.cinema.pricing.model.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionMapper {
    public TransactionResponse toResponse(TransactionCalculation calculation) {
        return TransactionResponse.builder()
                .transactionId(calculation.getTransactionId())
                .tickets(mapTicketCalculations(calculation.getTicketCalculations()))
                .totalCost(calculation.getTotalCost())
                .build();
    }

    private @Valid List<@Valid Ticket> mapTicketCalculations(List<TicketCalculation> ticketCalculations) {
        return ticketCalculations.stream()
                .map(this::toTicket)
                .collect(Collectors.toList());
    }

    private Ticket toTicket(TicketCalculation ticketCalculation) {
        return Ticket.builder()
                .ticketType(Ticket.TicketTypeEnum.valueOf(ticketCalculation.getTicketType().name()))
                .quantity(ticketCalculation.getQuantity())
                .totalCost(ticketCalculation.getTotalCost())
                .build();
    }
}
