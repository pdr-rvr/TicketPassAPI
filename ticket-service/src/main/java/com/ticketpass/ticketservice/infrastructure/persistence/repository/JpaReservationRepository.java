package com.ticketpass.ticketservice.infrastructure.persistence.repository;

import com.ticketpass.ticketservice.domain.model.ReservationStatus;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JpaReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime expiresAtBefore);
    List<ReservationEntity> findByUserId(Long userId);
    List<ReservationEntity> findByEventId(Long eventId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE ReservationEntity r SET r.status = :targetStatus WHERE r.eventId = :eventId AND r.status IN :sourceStatuses")
    int updateStatusByEventIdAndStatusIn(
            @org.springframework.data.repository.query.Param("eventId") Long eventId,
            @org.springframework.data.repository.query.Param("targetStatus") ReservationStatus targetStatus,
            @org.springframework.data.repository.query.Param("sourceStatuses") List<ReservationStatus> sourceStatuses
    );
}
