package com.ticket.transaction.mapper;

import com.ticket.transaction.domain.TicketCalculation;
import com.ticket.transaction.domain.TransactionCalculation;
import com.ticket.transaction.model.Ticket;
import com.ticket.transaction.model.TicketType;
import com.ticket.transaction.model.TransactionResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;

import java.util.Collections;
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
