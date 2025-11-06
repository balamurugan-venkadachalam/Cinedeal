package com.cinema.pricing.service.impl;

import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.PricingFact;
import com.cinema.pricing.domain.TicketCalculation;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.domain.TransactionCalculation;
import com.cinema.pricing.model.Customer;
import com.cinema.pricing.service.TicketPriceProvider;
import com.cinema.pricing.service.TicketPricingService;
import com.cinema.pricing.service.TicketTypeResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroolsTicketPricingService implements TicketPricingService {

    private final KieContainer kieContainer;
    private final TicketTypeResolver ticketTypeResolver;
    private final PricingConfiguration pricingConfiguration;
    private final TicketPriceProvider ticketPriceProvider;

    @Override
    public TransactionCalculation calculatePrice(Long transactionId, List<Customer> customers) {
        validateInput(transactionId, customers);

        Map<TicketType, Integer> ticketCounts = groupCustomersByTicketType(customers);

        List<PricingFact> pricingFacts = createPricingFacts(ticketCounts);

        executeRules(pricingFacts);

        List<TicketCalculation> ticketCalculations = convertToTicketCalculations(pricingFacts);

        // Calculate total cost
        double totalCost = ticketCalculations.stream()
                .mapToDouble(TicketCalculation::getTotalCost)
                .sum();

        totalCost = Math.round(totalCost * 100.0) / 100.0;

        log.debug("Transaction {} total cost: ${}", transactionId, totalCost);

        return TransactionCalculation.builder()
                .transactionId(transactionId)
                .ticketCalculations(ticketCalculations)
                .totalCost(totalCost)
                .build();
    }

    private List<TicketCalculation> convertToTicketCalculations(List<PricingFact> pricingFacts) {
        return pricingFacts.stream()
                .map( fact -> TicketCalculation.builder()
                                .ticketType(fact.getTicketType())
                                .quantity(fact.getQuantity())
                                .totalCost(fact.getCalculatedPrice())
                                .discountApplied(fact.isDiscountApplied())
                                .build())
                        .sorted(Comparator.comparing(TicketCalculation::getTicketTypeName))
                        .collect(Collectors.toList());
    }

    private void executeRules(List<PricingFact> pricingFacts) {
        KieSession kieSession = kieContainer.newKieSession();

        try {
            kieSession.setGlobal("config", pricingConfiguration);
            kieSession.setGlobal("log", log);

            pricingFacts.forEach(kieSession::insert);
            var rulesFired = kieSession.fireAllRules();
            log.debug("Fired {} rules", rulesFired);
        } finally {
            kieSession.dispose();
        }

    }

    private List<PricingFact> createPricingFacts(Map<TicketType, Integer> ticketCounts) {
        List<PricingFact> facts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (var entry :ticketCounts.entrySet()){
            TicketType ticketType = entry.getKey();
            int quantity = entry.getValue();
            double basePrice = ticketPriceProvider.getBasePrice(ticketType);

            PricingFact fact = PricingFact.builder()
                    .ticketType(ticketType)
                    .quantity(quantity)
                    .basePrice(basePrice)
                    .allTicketCounts(ticketCounts)
                    .transactionTime(now)
                    .calculatedPrice(0.0)
                    .appliedDiscounts(new ArrayList<>())
                    .discountApplied(false)
                    .build();

            facts.add(fact);
        }
        return facts;
    }

    private Map<TicketType, Integer> groupCustomersByTicketType(List<Customer> customers) {
        return customers.stream()
                .map(Customer::getAge)
                .map(ticketTypeResolver::resolveTicketType)
                .collect(
                        Collectors.groupingBy(
                                ticketType -> ticketType,
                                Collectors.summingInt(age -> 1)
                        )
                );
    }

    private void validateInput(Long transactionId, List<Customer> customers) {
        if (transactionId == null) {
            throw new IllegalArgumentException("Transaction ID cannot be null");
        }
        if (customers == null || customers.isEmpty()) {
            throw new IllegalArgumentException("Customers list cannot be null or empty");
        }

        for (Customer customer : customers) {
            if (customer.getAge() == null || customer.getAge() < 0) {
                throw new IllegalArgumentException("Customer age cannot be null");
            }
            if (customer.getAge() < 0) {
                throw new IllegalArgumentException("Customer age cannot be negative");
            }
            if (customer.getName() == null || customer.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name cannot be null or empty");
            }
        }
    }


}
