package br.com.loomi.paymentmicroservice.clients;

import br.com.loomi.paymentmicroservice.models.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-microservice", path = "/customer")
public interface CustomerClient {
    @GetMapping(path = "getById/{id}")
    ResponseEntity<Customer> findUserEmailByCustomerId(@PathVariable("id") UUID id);
}
