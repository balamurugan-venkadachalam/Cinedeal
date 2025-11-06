package com.cinema.pricing.service.impl;

import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.TicketCalculation;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.domain.TransactionCalculation;
import com.cinema.pricing.model.Customer;
import com.cinema.pricing.service.TicketPriceProvider;
import com.cinema.pricing.service.TicketTypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.api.runtime.KieContainer;

import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;

import org.kie.internal.io.ResourceFactory;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DroolsTicketPricingServiceTest {

    private KieContainer kieContainer;

    @Mock
    private TicketTypeResolver ticketTypeResolver;

    @Mock
    private TicketPriceProvider ticketPriceProvider;

    private PricingConfiguration pricingConfiguration;

    private DroolsTicketPricingService service;

    @BeforeEach
    void setUp() {

        // Initialize real KieContainer for Drools rules execution
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource("rules/pricing-rules.drl"));
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
        KieModule kieModule = kieBuilder.getKieModule();
        kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());


        pricingConfiguration = new PricingConfiguration();

        pricingConfiguration = new PricingConfiguration();
        pricingConfiguration.setAdultBasePrice(25.0);
        pricingConfiguration.setTeenBasePrice(15.00);
        pricingConfiguration.setChildrenBasePrice(5.0);
        pricingConfiguration.setSeniorBasePrice(25.00);

        // Mock ticket type resolver
        when(ticketTypeResolver.resolveTicketType(anyInt())).thenAnswer(invocation -> {
            int age = invocation.getArgument(0);
            if (age <= 10) return TicketType.CHILDREN;
            if (age <= 17) return TicketType.TEEN;
            if (age <= 64) return TicketType.ADULT;
            return TicketType.SENIOR;
        });

        service = new DroolsTicketPricingService(kieContainer, ticketTypeResolver, pricingConfiguration, ticketPriceProvider);

    }

    @Test
    void shouldCalculatePricesForMultipleTicketTypes() {
        // GIVEN
        List<Customer> customers = Arrays.asList(
                createCustomer("Adult 1", 30),
                createCustomer("Adult 2", 35),
                createCustomer("Teen 1", 15),
                createCustomer("Child 1", 8),
                createCustomer("Senior 1", 70)
        );

        when(ticketPriceProvider.getBasePrice(TicketType.ADULT)).thenReturn(25.0);
        when(ticketPriceProvider.getBasePrice(TicketType.TEEN)).thenReturn(15.0);
        when(ticketPriceProvider.getBasePrice(TicketType.CHILDREN)).thenReturn(5.0);
        when(ticketPriceProvider.getBasePrice(TicketType.SENIOR)).thenReturn(25.00);

        // WHEN
        TransactionCalculation result = service.calculatePrice(1L, customers);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTicketCalculations()).hasSize(4);

        assertThat(result.getTotalCost()).isEqualTo(87.50);
    }

    @Test
    @DisplayName("Should apply children bulk discount for 3 or more children")
    void shouldApplyChildrenBulkDiscount() {
        // GIVEN
        List<Customer> customers = Arrays.asList(
                createCustomer("Child 1", 5),
                createCustomer("Child 2", 7),
                createCustomer("Child 3", 9)
        );

        when(ticketPriceProvider.getBasePrice(TicketType.CHILDREN)).thenReturn(5.0);


        // WHEN
        TransactionCalculation result = service.calculatePrice(1L, customers);

        // THEN
        assertThat(result).isNotNull();

        TicketCalculation childrenTicket = result.getTicketCalculations().stream()
                .filter(tc -> tc.getTicketType() == TicketType.CHILDREN)
                .findFirst()
                .orElseThrow();

        assertThat(childrenTicket.getTotalCost()).isEqualTo(11.25);
        assertThat(childrenTicket.isDiscountApplied()).isTrue();
    }

    private Customer createCustomer(String name, Integer age) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAge(age);
        return customer;
    }


}
