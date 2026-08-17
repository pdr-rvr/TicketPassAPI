package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetEventDetailsUseCase {

    private final EventRepository eventRepository;

    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));
    }

    public List<Event> listAll() {
        return eventRepository.findAll();
    }

    public List<Event> search(String name, String location) {
        return eventRepository.findByNameAndLocation(name, location);
    }
}
