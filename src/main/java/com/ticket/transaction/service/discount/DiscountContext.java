package com.ticket.transaction.service.discount;

import com.ticket.transaction.domain.TicketType;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class DiscountContext {
    TicketType ticketType;
    int quantity;
    double basePrice;
    double baseTotalCost;
    Map<TicketType, Integer> allTicketCounts;
    LocalDateTime transactionTime;
    String promoCode;
}
