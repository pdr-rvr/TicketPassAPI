package com.ticketpass.ticketservice.infrastructure.persistence.mapper;

import com.ticketpass.ticketservice.domain.model.User;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.UserEntity;

public class UserMapper {
    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole())
                .build();
    }

    public static UserEntity toEntity(User domain) {
        if (domain == null) return null;
        return UserEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .role(domain.getRole())
                .build();
    }
}
