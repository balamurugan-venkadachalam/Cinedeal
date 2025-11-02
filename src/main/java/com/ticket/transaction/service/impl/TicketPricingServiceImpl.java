package com.ticket.transaction.service.impl;

import com.ticket.transaction.domain.TicketCalculation;
import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.domain.TransactionCalculation;
import com.ticket.transaction.model.Customer;
import com.ticket.transaction.service.TicketPricingService;
import com.ticket.transaction.service.TicketTypeResolver;
import com.ticket.transaction.service.PricingStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketPricingServiceImpl implements TicketPricingService {

    private final PricingStrategy pricingStrategy;

    private final TicketTypeResolver ticketTypeResolver;

    @Override
    public TransactionCalculation calculatePrice(Long transactionId, List<Customer> customers) {
        log.debug("Calculating ticket prices for transaction {} with {} customers",
                transactionId, customers.size());

        Map<TicketType, Integer> ticketCounts = groupCustomersByTicketType(customers);

        List<TicketCalculation> ticketCalculations = calculateTicketCosts(ticketCounts);

        double totalCost = ticketCalculations.stream()
                .mapToDouble(TicketCalculation::getTotalCost)
                .sum();
        totalCost = Math.round(totalCost * 100.0) / 100.0;

        log.debug("Total cost for transaction {} is {}", transactionId, totalCost);

        return TransactionCalculation.builder()
                .transactionId(transactionId)
                .ticketCalculations(ticketCalculations)
                .totalCost(totalCost)
                .build();
    }

    private List<TicketCalculation> calculateTicketCosts(Map<TicketType, Integer> ticketCounts) {
        return ticketCounts.entrySet().stream()
                .map(entry -> TicketCalculation.builder()
                        .ticketType(entry.getKey())
                        .quantity(entry.getValue())
                        .totalCost(pricingStrategy.calculateCost(entry.getKey(), entry.getValue(), ticketCounts))
                        .build())
                .sorted(Comparator.comparing(TicketCalculation::getTicketTypeName))
                .collect(Collectors.toList());
    }

    private Map<TicketType, Integer> groupCustomersByTicketType(List<Customer> customers) {
        return customers.stream()
                .map(Customer::getAge)
                .map(ticketTypeResolver::resolveTicketType)
                .collect(Collectors.groupingBy(
                        ticketType -> ticketType,
                        Collectors.summingInt(e -> 1)
                ));
    }
}
