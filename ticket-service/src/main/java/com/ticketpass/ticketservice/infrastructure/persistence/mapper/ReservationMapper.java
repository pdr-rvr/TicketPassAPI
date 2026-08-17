package com.ticketpass.ticketservice.infrastructure.persistence.mapper;

import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.ReservationEntity;

public class ReservationMapper {
    public static Reservation toDomain(ReservationEntity entity) {
        if (entity == null) return null;
        return Reservation.builder()
                .id(entity.getId())
                .eventId(entity.getEventId())
                .userId(entity.getUserId())
                .quantity(entity.getQuantity())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    public static ReservationEntity toEntity(Reservation domain) {
        if (domain == null) return null;
        return ReservationEntity.builder()
                .id(domain.getId())
                .eventId(domain.getEventId())
                .userId(domain.getUserId())
                .quantity(domain.getQuantity())
                .totalAmount(domain.getTotalAmount())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .build();
    }
}
