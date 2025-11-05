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
        "pricing.adult-base-price=25.0",
        "pricing.teen-base-price=10.0",
        "pricing.children-base-price=5.0",
        "pricing.senior-base-price=15.0"
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
        // Given
        List<Customer> customers = Arrays.asList(
                createCustomer("John Doe", 30)
        );

        // When
        TransactionCalculation result = pricingService.calculatePrice(1L, customers);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalCost()).isEqualTo(25.0);
        assertThat(result.getTicketCalculations()).hasSize(1);

        TicketCalculation ticket = result.getTicketCalculations().get(0);
        assertThat(ticket.getTicketType()).isEqualTo(TicketType.ADULT);
        assertThat(ticket.getQuantity()).isEqualTo(1);
        assertThat(ticket.getTotalCost()).isEqualTo(25.0);
    }

    private Customer createCustomer(String name, Integer age) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setAge(age);
        return customer;
    }

}
