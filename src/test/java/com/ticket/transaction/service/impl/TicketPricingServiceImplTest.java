package com.ticket.transaction.service.impl;

import com.ticket.transaction.domain.TicketType;
import com.ticket.transaction.domain.TransactionCalculation;
import com.ticket.transaction.model.Customer;
import com.ticket.transaction.service.PricingStrategy;
import com.ticket.transaction.service.TicketTypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketPricingServiceImplTest {

    @Mock
    private PricingStrategy pricingStrategy;

    @Mock
    private TicketTypeResolver ticketTypeResolver;

    private TicketPricingServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TicketPricingServiceImpl(pricingStrategy, ticketTypeResolver);
    }

    @Test
    void testCalculateTicketPrices_OneAdultTwoChild() {
        // GIVEN
        List<Customer> customers = new ArrayList<>();
        customers.add(createCustomer("John Smith", 70));
        customers.add(createCustomer("Jane Doe", 5));
        customers.add(createCustomer("Bob Doe", 6));

        when(ticketTypeResolver.resolveTicketType(70)).thenReturn(TicketType.SENIOR);
        when(ticketTypeResolver.resolveTicketType(5)).thenReturn(TicketType.CHILDREN);
        when(ticketTypeResolver.resolveTicketType(6)).thenReturn(TicketType.CHILDREN);

        when(pricingStrategy.calculateCost(eq(TicketType.SENIOR), eq(1), any(Map.class)))
                .thenReturn(17.50);
        when(pricingStrategy.calculateCost(eq(TicketType.CHILDREN), eq(2), any(Map.class)))
                .thenReturn(10.00);


        // WHEN
        TransactionCalculation result = service.calculatePrice(1L, customers);

        // THEN
        assertEquals(1L, result.getTransactionId());
        assertEquals(2, result.getTicketCalculations().size());
        assertEquals(27.50, result.getTotalCost());

    }

    private Customer createCustomer(String name, int age) {
        return Customer.builder()
                .name(name)
                .age(age)
                .build();
    }
}
