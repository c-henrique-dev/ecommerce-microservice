package br.com.loomi.customermicroservice.controllers;

import br.com.loomi.customermicroservice.services.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("userDetails")
public class CustomUserDetailsController {

    private CustomUserDetailsService customUserDetailsService;

    public CustomUserDetailsController(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Load user by email",
            description = "Fetches user details based on the provided email."
    )
    @Hidden
    public UserDetails loadByEmail(@PathVariable String email) {
        return customUserDetailsService.loadUserByUsername(email);
    }
}
