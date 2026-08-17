package com.ticketpass.ticketservice.domain.repository;

import com.ticketpass.ticketservice.domain.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    boolean existsById(Long id);
    java.util.List<User> findAll();
}
