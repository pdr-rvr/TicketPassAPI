package com.ticketpass.ticketservice.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UpdateEventRequest(
        String name,
        String description,
        LocalDateTime dateTime,
        String location,

        @Min(value = 1, message = "Price must be greater than zero")
        BigDecimal price
) {}
