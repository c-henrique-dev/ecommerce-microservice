package br.com.loomi.authmicroservice.controllers;

import br.com.loomi.authmicroservice.models.dtos.AuthDto;
import br.com.loomi.authmicroservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@Tag(name = "Auth", description = "Authentication and token management")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user using their email and password and returns a JWT token."
    )
    public ResponseEntity auth(@RequestBody @Valid AuthDto authDto) {
        return this.authService.auth(authDto);
    }
}
