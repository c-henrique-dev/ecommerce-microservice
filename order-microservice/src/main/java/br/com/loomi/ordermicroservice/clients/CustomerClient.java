package br.com.loomi.ordermicroservice.clients;

import br.com.loomi.ordermicroservice.models.dtos.CustomerDto;
import br.com.loomi.ordermicroservice.security.CustomUserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-microservice")
public interface CustomerClient {
    @GetMapping(path = "userDetails/{email}")
    ResponseEntity<CustomUserDetails> loadByEmail(@PathVariable("email") String email);

    @GetMapping(path = "customer/getById/{id}")
    ResponseEntity<CustomerDto> findUserEmailByCustomerId(@PathVariable("id") UUID id);

}
