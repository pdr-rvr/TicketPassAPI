package com.ticketpass.ticketservice.infrastructure.persistence.repository;

import com.ticketpass.ticketservice.infrastructure.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaOutboxEventRepository extends JpaRepository<OutboxEventEntity, Long> {
    List<OutboxEventEntity> findTop100ByOrderByIdAsc();
}
