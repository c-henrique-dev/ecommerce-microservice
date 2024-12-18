package br.com.loomi.paymentmicroservice.clients;

import br.com.loomi.paymentmicroservice.models.dtos.CustomerDto;
import br.com.loomi.paymentmicroservice.security.CustomUserDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-microservice")
public interface CustomerClient {
    @GetMapping(path = "customer/getById/{id}")
    ResponseEntity<CustomerDto> findUserEmailByCustomerId(@PathVariable("id") UUID id);

    @GetMapping(path = "userDetails/{email}")
    ResponseEntity<CustomUserDetails> loadByEmail(@PathVariable("email") String email);
}
