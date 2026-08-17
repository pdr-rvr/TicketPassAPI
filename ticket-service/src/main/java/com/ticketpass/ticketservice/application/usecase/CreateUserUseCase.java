package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.EmailAlreadyExistsException;
import com.ticketpass.ticketservice.domain.model.User;
import com.ticketpass.ticketservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateUserUseCase {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public User execute(Command command) {
        if (command.name() == null || command.name().trim().isEmpty()) {
            throw new IllegalArgumentException("User name cannot be empty");
        }
        if (command.email() == null || command.email().trim().isEmpty()) {
            throw new IllegalArgumentException("User email cannot be empty");
        }
        if (command.password() == null || command.password().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        userRepository.findByEmail(command.email().trim().toLowerCase()).ifPresent(u -> {
            throw new EmailAlreadyExistsException("Email already registered: " + command.email());
        });

        User user = User.builder()
                .name(command.name().trim())
                .email(command.email().trim().toLowerCase())
                .password(passwordEncoder.encode(command.password()))
                .role("ROLE_USER")
                .build();

        return userRepository.save(user);
    }

    public record Command(String name, String email, String password) {}
}
