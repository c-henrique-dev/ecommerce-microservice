package br.com.loomi.ordermicroservice.controllers;

import br.com.loomi.ordermicroservice.models.dtos.PaymentDto;
import br.com.loomi.ordermicroservice.services.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("order/payment")
@Tag(name = "Payment", description = "Endpoints related to simulating payments for an order")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Simulate a payment for an order",
            description = "This endpoint simulates payment for an order. If successful, debits stock and updates the order status to RECEIVED; otherwise, updates the status to PREPARATION."
    )
    @PostMapping("{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Map> simulatePayment(
            @PathVariable @Parameter(description = "Order identifier for payment") UUID orderId) throws JsonProcessingException {
        return paymentService.simulatePayment(orderId);
    }
}
