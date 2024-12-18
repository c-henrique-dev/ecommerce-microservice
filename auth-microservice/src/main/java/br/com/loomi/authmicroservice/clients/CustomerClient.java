package br.com.loomi.authmicroservice.clients;

import br.com.loomi.authmicroservice.models.dtos.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "customer-microservice", path = "/customer")
public interface CustomerClient {
    @GetMapping(path = "get/{email}")
    ResponseEntity<CustomerDto> loadByEmail(@PathVariable("email") String email);
}
