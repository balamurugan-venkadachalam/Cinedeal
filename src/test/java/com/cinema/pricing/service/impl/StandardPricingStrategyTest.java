package com.cinema.pricing.service.impl;

import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.service.discount.DiscountEngine;
import com.cinema.pricing.service.discount.impl.BulkDiscountStrategy;
import com.cinema.pricing.service.discount.impl.SeniorDiscountStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StandardPricingStrategyTest {

    private StandardPricingStrategy pricingStrategy;

    @BeforeEach
    void setUp() {
        PricingConfiguration config = new PricingConfiguration();
        config.setAdultBasePrice(25.00);
        config.setSeniorBasePrice(25.00);
        config.setTeenBasePrice(12.00);
        config.setChildrenBasePrice(5.00);
        config.setSeniorDiscountRate(0.30);

        ConfigBasedTicketPriceProvider priceProvider = new ConfigBasedTicketPriceProvider(config);
        BulkDiscountStrategy bulkDiscountStrategy = new BulkDiscountStrategy(config);
        SeniorDiscountStrategy seniorDiscountStrategy = new SeniorDiscountStrategy(config);
        
        DiscountEngine discountEngine = new DiscountEngine(List.of(bulkDiscountStrategy, seniorDiscountStrategy));
        discountEngine.init();

        pricingStrategy = new StandardPricingStrategy(config, priceProvider, discountEngine);
        pricingStrategy.init();
    }


    @Test
    void testCalculateCost_AdultTicket() {
        //GIVEN
        Map<TicketType, Integer> ticketCounts = new HashMap<>();
        ticketCounts.put(TicketType.ADULT, 1);

        //WHEN
        double cost = pricingStrategy.calculateCost(TicketType.ADULT, 1, ticketCounts);

        //THEN
        assertEquals(25.00, cost);
    }

    @Test
    void testCalculateCost_SeniorTicket() {
        //GIVEN
        Map<TicketType, Integer> ticketCounts = new HashMap<>();
        ticketCounts.put(TicketType.SENIOR, 1);

        //WHEN
        double cost = pricingStrategy.calculateCost(TicketType.SENIOR, 1, ticketCounts);

        //THEN
        assertEquals(17.50, cost);
    }

    @Test
    void testCalculateCost_TeenTicket() {
        //GIVEN
        Map<TicketType, Integer> ticketCounts = new HashMap<>();
        ticketCounts.put(TicketType.TEEN, 1);

        //WHEN
        double cost = pricingStrategy.calculateCost(TicketType.TEEN, 1, ticketCounts);

        //THEN
        assertEquals(12.00, cost);
    }

    @Test
    void testCalculateCost_ChildrenTicket_NoDiscount() {
        Map<TicketType, Integer> ticketCounts = new HashMap<>();
        ticketCounts.put(TicketType.CHILDREN, 2);

        double cost = pricingStrategy.calculateCost(TicketType.CHILDREN, 2, ticketCounts);
        assertEquals(10.00, cost);
        assertFalse(pricingStrategy.isDiscountApplied(TicketType.CHILDREN, 2, ticketCounts));
    }


    @Test
    void testCalculateCost_ChildrenTicket_WithDiscount() {
        //GIVEN
        Map<TicketType, Integer> ticketCounts = new HashMap<>();
        ticketCounts.put(TicketType.CHILDREN, 3);

        //WHEN
        double cost = pricingStrategy.calculateCost(TicketType.CHILDREN, 3, ticketCounts);
        assertEquals(11.25, cost);

        //THEN
        assertTrue(pricingStrategy.isDiscountApplied(TicketType.CHILDREN, 3, ticketCounts));
    }


    @Test
    void testCalculateCost_MultipleTicketTypes() {
        //GIVEN
        Map<TicketType, Integer> ticketCounts = new HashMap<>();
        ticketCounts.put(TicketType.ADULT, 1);
        ticketCounts.put(TicketType.CHILDREN, 3);

        //WHEN
        double adultCost = pricingStrategy.calculateCost(TicketType.ADULT, 1, ticketCounts);
        double childrenCost = pricingStrategy.calculateCost(TicketType.CHILDREN, 3, ticketCounts);

        //THEN
        assertEquals(25.00, adultCost);
        assertEquals(11.25, childrenCost);
    }

}
