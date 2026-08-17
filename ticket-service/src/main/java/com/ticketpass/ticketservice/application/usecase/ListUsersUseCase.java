package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.model.User;
import com.ticketpass.ticketservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public List<User> execute() {
        return userRepository.findAll();
    }
}
