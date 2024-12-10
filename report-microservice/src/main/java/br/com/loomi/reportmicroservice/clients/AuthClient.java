package br.com.loomi.reportmicroservice.clients;

import br.com.loomi.reportmicroservice.security.CustomUserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-microservice", path = "/auth")
public interface AuthClient {

    @GetMapping(path = "/{email}")
    ResponseEntity<CustomUserDetails> loadByUsername(@PathVariable("email") String email);

    @GetMapping(path = "/valid/{token}")
    ResponseEntity<String> validToken(@PathVariable("token") String token);
}
