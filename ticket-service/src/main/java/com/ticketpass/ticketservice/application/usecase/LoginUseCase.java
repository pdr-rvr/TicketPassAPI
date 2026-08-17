package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.User;
import com.ticketpass.ticketservice.domain.repository.UserRepository;
import com.ticketpass.ticketservice.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public Response execute(Command command) {
        User user = userRepository.findByEmail(command.email().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + command.email()));

        if (user.getPassword() == null || !passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        return new Response(token, user);
    }

    public record Command(String email, String password) {}
    public record Response(String token, User user) {}
}
