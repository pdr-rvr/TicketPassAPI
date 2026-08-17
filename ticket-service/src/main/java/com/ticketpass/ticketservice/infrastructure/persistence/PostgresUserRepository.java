package com.ticketpass.ticketservice.infrastructure.persistence;

import com.ticketpass.ticketservice.domain.model.User;
import com.ticketpass.ticketservice.domain.repository.UserRepository;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.UserEntity;
import com.ticketpass.ticketservice.infrastructure.persistence.mapper.UserMapper;
import com.ticketpass.ticketservice.infrastructure.persistence.repository.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PostgresUserRepository implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = jpaUserRepository.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaUserRepository.existsById(id);
    }

    @Override
    public java.util.List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(UserMapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }
}
