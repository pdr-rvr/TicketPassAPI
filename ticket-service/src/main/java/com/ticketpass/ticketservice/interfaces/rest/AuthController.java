package com.ticketpass.ticketservice.interfaces.rest;

import com.ticketpass.ticketservice.application.usecase.LoginUseCase;
import com.ticketpass.ticketservice.interfaces.rest.dto.LoginRequest;
import com.ticketpass.ticketservice.interfaces.rest.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginUseCase.Command command = new LoginUseCase.Command(request.email(), request.password());
        LoginUseCase.Response result = loginUseCase.execute(command);
        
        LoginResponse response = new LoginResponse(
                result.token(),
                result.user().getId(),
                result.user().getName(),
                result.user().getEmail(),
                result.user().getRole()
        );
        return ResponseEntity.ok(response);
    }
}
