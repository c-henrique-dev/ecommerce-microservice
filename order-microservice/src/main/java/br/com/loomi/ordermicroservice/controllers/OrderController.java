package br.com.loomi.ordermicroservice.controllers;

import br.com.loomi.ordermicroservice.models.dtos.OrderWithProductDTO;
import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.Order;
import br.com.loomi.ordermicroservice.models.enums.OrderStatus;
import br.com.loomi.ordermicroservice.services.CartService;
import br.com.loomi.ordermicroservice.services.OrderService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("order")
@Tag(name = "Order", description = "Endpoints related to order processing and management")
public class OrderController {

    private OrderService orderService;
    private CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @Operation(
            summary = "Checkout process",
            description = "Creates an order from the customer's shopping cart. Only customers or admins can perform this action."
    )
    @PostMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Order> checkout(
            @Parameter(description = "Customer ID to initiate checkout", required = true)
            @PathVariable UUID customerId) {

        Cart cart = cartService.getCart(customerId);

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Order order = orderService.createOrderFromCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @Operation(
            summary = "Get order by ID",
            description = "Retrieves order details by order ID. Only customers or admins can perform this action."
    )
    @GetMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Order> getById(
            @Parameter(description = "Order ID to fetch the order details", required = true)
            @PathVariable UUID orderId) {

        Order order = this.orderService.findById(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @Operation(
            summary = "Update the status of an order",
            description = "Updates the status of a specific order using its ID. The new status must be provided as a query parameter."
    )
    @PatchMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public void updateOrderStatus(
            @Parameter(description = "Order ID to update the status", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "New status to be set for the order", required = true)
            @RequestParam OrderStatus newStatus) {
        this.orderService.updateOrderStatus(orderId, newStatus);
    }

    @GetMapping("/period")
    @Hidden
    @ResponseStatus(HttpStatus.OK)
    public List<OrderWithProductDTO> getOrdersByPeriod(@RequestParam("initialDate") LocalDate initialDate,
                                                       @RequestParam("endDate") LocalDate endDate) {
        return orderService.findOrdersByPeriod(initialDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }
}
