package com.cinema.pricing.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingFact {

    private TicketType ticketType;
    private int quantity;
    private double basePrice;
    private Map<TicketType, Integer> allTicketCounts;
    private LocalDateTime transactionTime;
    private double calculatedPrice;
    private List<String> appliedDiscounts = new ArrayList<>();
    private boolean discountApplied;
}
