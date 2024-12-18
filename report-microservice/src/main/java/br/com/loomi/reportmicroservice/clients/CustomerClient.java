package br.com.loomi.reportmicroservice.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-microservice", path = "/userDetails")
public interface CustomerClient {
    @GetMapping(path = "{email}")
    ResponseEntity<br.com.loomi.reportmicroservice.security.CustomUserDetails> loadByEmail(@PathVariable("email") String email);
}
