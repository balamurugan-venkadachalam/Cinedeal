package com.cinema.pricing.service;

import com.cinema.pricing.domain.TransactionCalculation;
import com.cinema.pricing.model.Customer;

import java.util.List;

public interface TicketPricingService {

    TransactionCalculation calculatePrice(Long transactionId, List<Customer> customers);

}
