package com.ticketpass.ticketservice.infrastructure.persistence.mapper;

import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.EventEntity;

public class EventMapper {
    public static Event toDomain(EventEntity entity) {
        if (entity == null) return null;
        return Event.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .dateTime(entity.getDateTime())
                .location(entity.getLocation())
                .totalTickets(entity.getTotalTickets())
                .availableTickets(entity.getAvailableTickets())
                .price(entity.getPrice())
                .status(entity.getStatus())
                .build();
    }

    public static EventEntity toEntity(Event domain) {
        if (domain == null) return null;
        return EventEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .dateTime(domain.getDateTime())
                .location(domain.getLocation())
                .totalTickets(domain.getTotalTickets())
                .availableTickets(domain.getAvailableTickets())
                .price(domain.getPrice())
                .status(domain.getStatus())
                .build();
    }
}
