package com.ticketpass.ticketservice.interfaces.rest;

import com.ticketpass.ticketservice.application.usecase.CreateUserUseCase;
import com.ticketpass.ticketservice.application.usecase.GetUserDetailsUseCase;
import com.ticketpass.ticketservice.application.usecase.ListUsersUseCase;
import com.ticketpass.ticketservice.interfaces.rest.dto.CreateUserRequest;
import com.ticketpass.ticketservice.interfaces.rest.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserDetailsUseCase getUserDetailsUseCase;
    private final ListUsersUseCase listUsersUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserUseCase.Command command = new CreateUserUseCase.Command(
                request.name(),
                request.email(),
                request.password()
        );
        UserResponse response = UserResponse.fromDomain(createUserUseCase.execute(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<java.util.List<UserResponse>> listAllUsers() {
        java.util.List<UserResponse> response = listUsersUseCase.execute().stream()
                .map(UserResponse::fromDomain)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserDetails(@PathVariable Long id) {
        UserResponse response = UserResponse.fromDomain(getUserDetailsUseCase.getById(id));
        return ResponseEntity.ok(response);
    }
}
