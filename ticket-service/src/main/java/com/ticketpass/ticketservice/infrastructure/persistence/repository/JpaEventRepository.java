package com.ticketpass.ticketservice.infrastructure.persistence.repository;

import com.ticketpass.ticketservice.infrastructure.persistence.entity.EventEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaEventRepository extends JpaRepository<EventEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
    Optional<EventEntity> findByIdForUpdate(@Param("id") Long id);

    java.util.List<EventEntity> findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(String name, String location);
}
