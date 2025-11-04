package com.cinema.pricing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "pricing")
@Getter
@Setter
public class PricingConfiguration {

    private double adultBasePrice = 25.00;
    private double seniorBasePrice = 17.50;
    private double teenBasePrice = 15.00;
    private double childrenBasePrice = 5.00;
    private double seniorDiscountRate = 0.30;

    private Map<String, AgeRange> ageRanges = new HashMap<>();
    private List<BulkDiscountConfig> bulkDiscounts = new ArrayList<>();
}

