package com.ticket.transaction.service;

import com.ticket.transaction.domain.TransactionCalculation;
import com.ticket.transaction.model.Customer;

import java.util.List;

public interface TicketPricingService {

    TransactionCalculation calculatePrice(Long transactionId, List<Customer> customers);

}
