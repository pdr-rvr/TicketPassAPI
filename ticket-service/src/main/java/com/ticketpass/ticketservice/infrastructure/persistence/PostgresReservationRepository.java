package com.ticketpass.ticketservice.infrastructure.persistence;

import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.model.ReservationStatus;
import com.ticketpass.ticketservice.domain.repository.ReservationRepository;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.ReservationEntity;
import com.ticketpass.ticketservice.infrastructure.persistence.mapper.ReservationMapper;
import com.ticketpass.ticketservice.infrastructure.persistence.repository.JpaReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostgresReservationRepository implements ReservationRepository {

    private final JpaReservationRepository jpaReservationRepository;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = ReservationMapper.toEntity(reservation);
        ReservationEntity saved = jpaReservationRepository.save(entity);
        return ReservationMapper.toDomain(saved);
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaReservationRepository.findById(id)
                .map(ReservationMapper::toDomain);
    }

    @Override
    public List<Reservation> findAllExpired(LocalDateTime now) {
        return jpaReservationRepository.findByStatusAndExpiresAtBefore(ReservationStatus.PENDING, now)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return jpaReservationRepository.findByUserId(userId)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByEventId(Long eventId) {
        return jpaReservationRepository.findByEventId(eventId)
                .stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int cancelAllActiveByEventId(Long eventId) {
        return jpaReservationRepository.updateStatusByEventIdAndStatusIn(
                eventId,
                ReservationStatus.CANCELED,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );
    }
}
