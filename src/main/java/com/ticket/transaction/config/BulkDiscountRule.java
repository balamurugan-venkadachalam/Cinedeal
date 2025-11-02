package com.ticket.transaction.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkDiscountRule {
    private String ticketType;
    private int quantity;
    //e.g., 0.25 for 25% off
    private double discountRate;
}
