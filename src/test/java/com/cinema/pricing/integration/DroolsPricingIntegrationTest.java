package com.cinema.pricing.integration;

import com.cinema.pricing.config.DroolsApplicationConfig;
import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.TicketCalculation;
import com.cinema.pricing.domain.TicketType;
import com.cinema.pricing.domain.TransactionCalculation;
import com.cinema.pricing.model.Customer;
import com.cinema.pricing.service.TicketPriceProvider;
import com.cinema.pricing.service.impl.ConfigBasedTicketPriceProvider;
import com.cinema.pricing.service.impl.ConfigBasedTicketTypeResolver;
import com.cinema.pricing.service.impl.DroolsTicketPricingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
        DroolsApplicationConfig.class,
        DroolsTicketPricingService.class,
        ConfigBasedTicketTypeResolver.class,
        ConfigBasedTicketPriceProvider.class,
        TicketPriceProvider.class,
        PricingConfiguration.class
})
@TestPropertySource(properties = {
        "pricing.adult-base-price=25.00",
        "pricing.teen-base-price=10.00",
        "pricing.children-base-price=5.00",
        "pricing.senior-base-price=25.00"
})
@DisplayName("Drools Pricing Integration Tests")
public class DroolsPricingIntegrationTest {
    @Autowired
    private DroolsTicketPricingService pricingService;

    @Autowired
    private KieContainer kieContainer;

    @Test
    @DisplayName("Should execute Drools rules for single adult ticket")
    void shouldExecuteDroolsRulesForSingleAdult() {
        // GIVEN
        List<Customer> customers = List.of(
                createCustomer("Adult", 30)
        );

        // WHEN
        TransactionCalculation result = pricingService.calculatePrice(1L, customers);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTotalCost()).isEqualTo(25.0);
        assertThat(result.getTicketCalculations()).hasSize(1);

        TicketCalculation ticket = result.getTicketCalculations().getFirst();
        assertThat(ticket.getTicketType()).isEqualTo(TicketType.ADULT);
        assertThat(ticket.getQuantity()).isEqualTo(1);
        assertThat(ticket.getTotalCost()).isEqualTo(25.0);
    }


    @Test
    @DisplayName("Should execute Drools rules for children bulk discount")
    void shouldExecuteDroolsRulesForChildrenBulkDiscount() {
        // GIVEN
        List<Customer> customers = Arrays.asList(
                createCustomer("Child 1", 5),
                createCustomer("Child 2", 7),
                createCustomer("Child 3", 9)
        );

        // WHEN
        TransactionCalculation result = pricingService.calculatePrice(1L, customers);

        // THEN
        assertThat(result).isNotNull();

        TicketCalculation childrenTicket = result.getTicketCalculations().stream()
                .filter(tc -> tc.getTicketType() == TicketType.CHILDREN)
                .findFirst()
                .orElseThrow();

        assertThat(childrenTicket.getQuantity()).isEqualTo(3);
        assertThat(childrenTicket.getTotalCost()).isEqualTo(11.25);
        assertThat(childrenTicket.isDiscountApplied()).isTrue();
    }

    @Test
    @DisplayName("Should execute Drools rules for mixed ticket types")
    void shouldExecuteDroolsRulesForMixedTicketTypes() {
        // GIVEN
        List<Customer> customers = Arrays.asList(
                createCustomer("Adult 1", 36),
                createCustomer("Teen 1", 15),
                createCustomer("Child 1", 10),
                createCustomer("Senior 1", 95)
        );

        // WHEN
        TransactionCalculation result = pricingService.calculatePrice(1L, customers);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTicketCalculations()).hasSize(4);

        assertThat(result.getTotalCost()).isEqualTo(59.50);
    }

    @Test
    @DisplayName("Should execute Drools rules for family transaction from spec")
    void shouldExecuteDroolsRulesForFamilyTransaction() {
        // GIVEN
        List<Customer> customers = Arrays.asList(
                createCustomer("Senior", 70),
                createCustomer("Child 1", 5),
                createCustomer("Child 2", 6)
        );

        // WHEN
        TransactionCalculation result = pricingService.calculatePrice(1L, customers);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getTicketCalculations()).hasSize(2);

        assertThat(result.getTotalCost()).isEqualTo(27.50);
    }

    private Customer createCustomer(String name, Integer age) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAge(age);
        return customer;
    }

}
