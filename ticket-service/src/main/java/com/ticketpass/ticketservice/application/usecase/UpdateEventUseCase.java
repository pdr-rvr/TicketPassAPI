package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import com.ticketpass.ticketservice.domain.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UpdateEventUseCase {

    private final EventRepository eventRepository;
    private final LockService lockService;
    private final TransactionTemplate transactionTemplate;

    public Event execute(Long id, Command command) {
        String lockKey = "lock:event:" + id;

        return lockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
            return transactionTemplate.execute(status -> {
                Event event = eventRepository.findByIdForUpdate(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + id));

                event.updateDetails(
                        command.name(),
                        command.description(),
                        command.dateTime(),
                        command.location(),
                        command.price()
                );

                return eventRepository.save(event);
            });
        });
    }

    public record Command(String name, String description, LocalDateTime dateTime, String location, BigDecimal price) {}
}
