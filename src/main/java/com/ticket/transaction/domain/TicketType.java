package com.ticket.transaction.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
@RequiredArgsConstructor
public enum TicketType {
    ADULT("Adult"),
    SENIOR("Senior"),
    TEEN("Teen"),
    CHILDREN("Children");

    private final String displayName;
}

