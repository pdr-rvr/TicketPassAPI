package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateEventUseCase {

    private final EventRepository eventRepository;

    @Transactional
    public Event execute(Command command) {
        Event event = Event.builder()
                .name(command.name())
                .description(command.description())
                .dateTime(command.dateTime())
                .location(command.location())
                .totalTickets(command.totalTickets())
                .availableTickets(command.totalTickets())
                .price(command.price())
                .build();
        return eventRepository.save(event);
    }

    public record Command(
            String name,
            String description,
            LocalDateTime dateTime,
            String location,
            Integer totalTickets,
            BigDecimal price
    ) {}
}
