package com.ticketpass.ticketservice.interfaces.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveTicketsRequest(
    @NotNull(message = "Event ID is required")
    Long eventId,

    @NotNull(message = "User ID is required")
    Long userId,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {}
