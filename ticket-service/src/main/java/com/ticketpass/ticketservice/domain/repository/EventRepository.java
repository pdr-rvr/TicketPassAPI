package com.ticketpass.ticketservice.domain.repository;

import com.ticketpass.ticketservice.domain.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Event save(Event event);
    Optional<Event> findById(Long id);
    Optional<Event> findByIdForUpdate(Long id);
    List<Event> findAll();
    List<Event> findByNameAndLocation(String name, String location);
}
