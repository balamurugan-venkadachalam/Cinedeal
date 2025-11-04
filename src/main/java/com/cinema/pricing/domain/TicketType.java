package com.cinema.pricing.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TicketType {
    ADULT("Adult"),
    SENIOR("Senior"),
    TEEN("Teen"),
    CHILDREN("Children");

    private final String displayName;
}

