package com.cinema.pricing.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgeRange {
    private int min;
    private int max;

    public boolean contains(int age) {
        return age >= min && age <= max;
    }


    @Override
    public String toString() {
        return min + "-" + max;
    }
}
