package com.cinema.pricing.service.impl;

import com.cinema.pricing.config.PricingConfiguration;
import com.cinema.pricing.domain.TicketType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigBasedTicketTypeResolverTest {

    private ConfigBasedTicketTypeResolver resolver;

    @BeforeEach
    void setUp() {
        PricingConfiguration config = new PricingConfiguration();
        resolver = new ConfigBasedTicketTypeResolver(config);
        resolver.init();
    }


    @ParameterizedTest
    @CsvSource({
            "0, CHILDREN",
            "5, CHILDREN",
            "10, CHILDREN",
            "11, TEEN",
            "15, TEEN",
            "17, TEEN",
            "18, ADULT",
            "35, ADULT",
            "64, ADULT",
            "65, SENIOR",
            "70, SENIOR",
            "100, SENIOR"
    })
    void testResolveTicketType_ValidAges(int age, TicketType expectedType) {
        TicketType result = resolver.resolveTicketType(age);
        assertEquals(expectedType, result);
    }

}
