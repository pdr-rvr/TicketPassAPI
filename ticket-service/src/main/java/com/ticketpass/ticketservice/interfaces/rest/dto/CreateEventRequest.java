package com.ticketpass.ticketservice.interfaces.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventRequest(
    @NotBlank(message = "Name cannot be blank")
    String name,

    String description,

    @NotNull(message = "Date and time are required")
    LocalDateTime dateTime,

    @NotBlank(message = "Location cannot be blank")
    String location,

    @NotNull(message = "Total tickets is required")
    @Min(value = 1, message = "Total tickets must be at least 1")
    Integer totalTickets,

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price cannot be negative")
    BigDecimal price
) {}
