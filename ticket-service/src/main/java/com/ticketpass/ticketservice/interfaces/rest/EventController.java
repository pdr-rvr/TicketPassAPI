package com.ticketpass.ticketservice.interfaces.rest;

import com.ticketpass.ticketservice.application.usecase.CancelEventUseCase;
import com.ticketpass.ticketservice.application.usecase.CreateEventUseCase;
import com.ticketpass.ticketservice.application.usecase.GetEventDetailsUseCase;
import com.ticketpass.ticketservice.application.usecase.GetReservationUseCase;
import com.ticketpass.ticketservice.application.usecase.UpdateEventUseCase;
import com.ticketpass.ticketservice.interfaces.rest.dto.CreateEventRequest;
import com.ticketpass.ticketservice.interfaces.rest.dto.EventResponse;
import com.ticketpass.ticketservice.interfaces.rest.dto.ReservationResponse;
import com.ticketpass.ticketservice.interfaces.rest.dto.UpdateEventRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final CreateEventUseCase createEventUseCase;
    private final GetEventDetailsUseCase getEventDetailsUseCase;
    private final CancelEventUseCase cancelEventUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final UpdateEventUseCase updateEventUseCase;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request) {
        CreateEventUseCase.Command command = new CreateEventUseCase.Command(
                request.name(),
                request.description(),
                request.dateTime(),
                request.location(),
                request.totalTickets(),
                request.price()
        );
        EventResponse response = EventResponse.fromDomain(createEventUseCase.execute(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> listEvents() {
        List<EventResponse> response = getEventDetailsUseCase.listAll().stream()
                .map(EventResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventDetails(@PathVariable Long id) {
        EventResponse response = EventResponse.fromDomain(getEventDetailsUseCase.getById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelEvent(@PathVariable Long id) {
        cancelEventUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<List<ReservationResponse>> getEventReservations(@PathVariable Long id) {
        List<ReservationResponse> response = getReservationUseCase.getByEventId(id).stream()
                .map(ReservationResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchEvents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location
    ) {
        List<EventResponse> response = getEventDetailsUseCase.search(name, location).stream()
                .map(EventResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request
    ) {
        UpdateEventUseCase.Command command = new UpdateEventUseCase.Command(
                request.name(),
                request.description(),
                request.dateTime(),
                request.location(),
                request.price()
        );
        EventResponse response = EventResponse.fromDomain(updateEventUseCase.execute(id, command));
        return ResponseEntity.ok(response);
    }
}
