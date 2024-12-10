package br.com.loomi.ordermicroservice.clients;

import br.com.loomi.ordermicroservice.models.dtos.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "product-microservice", path = "product")
public interface ProductClient {

    @GetMapping("{id}")
    ResponseEntity<ProductDto> findById(@PathVariable UUID id);

    @PostMapping("{id}")
    void debitStock(
            @PathVariable UUID id,
            @RequestParam Integer qtd
    );
}
