package com.ticketpass.ticketservice.interfaces.rest.dto;

public record LoginResponse(
        String token,
        Long userId,
        String name,
        String email,
        String role
) {}
