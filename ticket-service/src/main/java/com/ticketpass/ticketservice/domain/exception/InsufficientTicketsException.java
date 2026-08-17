package com.ticketpass.ticketservice.domain.exception;

public class InsufficientTicketsException extends RuntimeException {
    public InsufficientTicketsException(String message) {
        super(message);
    }
}
