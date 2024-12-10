package br.com.loomi.authmicroservice.controllers;

import br.com.loomi.authmicroservice.models.dtos.AuthDto;
import br.com.loomi.authmicroservice.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "Auth", description = "Authentication and token management")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Load user by email",
            description = "Fetches user details based on the provided email."
    )
    public UserDetails loadByUsername(@PathVariable String email) {
        return authService.loadUserDetailByEmail(email);
    }

    @GetMapping("valid/{token}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Validate token",
            description = "Validates the provided authentication token."
    )
    public String validToken(@PathVariable String token) {
        return authService.validToken(token);
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
