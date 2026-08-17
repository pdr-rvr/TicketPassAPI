package com.ticketpass.ticketservice.interfaces.rest.dto;

import com.ticketpass.ticketservice.domain.model.User;

public record UserResponse(Long id, String name, String email) {
    public static UserResponse fromDomain(User user) {
        if (user == null) return null;
        return new UserResponse(user.getId(), user.getName(), user.getEmail());
    }
}
