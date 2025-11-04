package com.cinema.pricing.service.impl;

import com.cinema.pricing.domain.TicketCalculation;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.domain.TransactionCalculation;
import com.cinema.pricing.model.Customer;
import com.cinema.pricing.service.PricingStrategy;
import com.cinema.pricing.service.TicketPricingService;
import com.cinema.pricing.service.TicketTypeResolver;
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
