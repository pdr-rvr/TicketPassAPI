package com.ticketpass.ticketservice.domain.model;

import com.ticketpass.ticketservice.domain.exception.InsufficientTicketsException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private Integer totalTickets;
    private Integer availableTickets;
    private BigDecimal price;
    @Builder.Default
    private String status = "ACTIVE";

    public void reserveTickets(int quantity) {
        if ("CANCELED".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Cannot reserve tickets for a canceled event: " + name);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (this.availableTickets < quantity) {
            throw new InsufficientTicketsException("Insufficient tickets available for event: " + name);
        }
        this.availableTickets -= quantity;
    }

    public void cancel() {
        if ("CANCELED".equalsIgnoreCase(this.status)) {
            return;
        }
        this.status = "CANCELED";
    }

    public void updateDetails(String name, String description, LocalDateTime dateTime, String location, java.math.BigDecimal price) {
        if ("CANCELED".equalsIgnoreCase(this.status)) {
            throw new IllegalStateException("Cannot update details of a canceled event: " + this.name);
        }
        if (price != null && price.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        if (name != null && !name.trim().isEmpty()) {
            this.name = name.trim();
        }
        if (description != null) {
            this.description = description.trim();
        }
        if (dateTime != null) {
            this.dateTime = dateTime;
        }
        if (location != null && !location.trim().isEmpty()) {
            this.location = location.trim();
        }
        if (price != null) {
            this.price = price;
        }
    }

    public void releaseTickets(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (this.availableTickets + quantity > this.totalTickets) {
            this.availableTickets = this.totalTickets;
        } else {
            this.availableTickets += quantity;
        }
    }
}
