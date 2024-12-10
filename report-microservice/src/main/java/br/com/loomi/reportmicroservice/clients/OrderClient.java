package br.com.loomi.reportmicroservice.clients;

import br.com.loomi.reportmicroservice.models.dtos.OrderWithProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "order-microservice", path = "/order")
public interface OrderClient {

    @GetMapping("/period")
    ResponseEntity<List<OrderWithProductDTO>> getOrdersByPeriod(
            @RequestParam("initialDate") LocalDate initialDate,
            @RequestParam("endDate") LocalDate endDate
    );
}