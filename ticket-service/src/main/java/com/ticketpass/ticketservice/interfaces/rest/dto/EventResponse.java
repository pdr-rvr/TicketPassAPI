package com.ticketpass.ticketservice.interfaces.rest.dto;

import com.ticketpass.ticketservice.domain.model.Event;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
    Long id,
    String name,
    String description,
    LocalDateTime dateTime,
    String location,
    Integer totalTickets,
    Integer availableTickets,
    BigDecimal price,
    String status
) {
    public static EventResponse fromDomain(Event event) {
        if (event == null) return null;
        return new EventResponse(
            event.getId(),
            event.getName(),
            event.getDescription(),
            event.getDateTime(),
            event.getLocation(),
            event.getTotalTickets(),
            event.getAvailableTickets(),
            event.getPrice(),
            event.getStatus()
        );
    }
}
