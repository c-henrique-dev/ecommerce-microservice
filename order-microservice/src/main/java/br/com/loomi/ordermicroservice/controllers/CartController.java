package br.com.loomi.ordermicroservice.controllers;

import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("order/cart")
@Tag(name = "Cart", description = "Endpoints related to shopping cart management")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(
            summary = "Retrieve the shopping cart of a customer",
            description = "Returns the shopping cart associated with the provided customer ID"
    )
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Cart> getCart(
            @Parameter(description = "Customer ID to retrieve the shopping cart", required = true)
            @PathVariable UUID customerId) {

        Cart cart = cartService.getCart(customerId);
        return ResponseEntity.ok(cart);
    }

    @Operation(
            summary = "Add a product to the shopping cart",
            description = "Adds a specified product to the customer's shopping cart with the given quantity"
    )
    @PostMapping("{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Cart> addToCart(
            @Parameter(description = "Customer ID to add the product to the cart", required = true)
            @PathVariable UUID customerId,

            @Parameter(description = "Product ID to add to the shopping cart", required = true)
            @RequestParam UUID productId,

            @Parameter(description = "Quantity of the product to add", required = true)
            @RequestParam Integer quantity) {

        ResponseEntity<Cart> updatedCart = cartService.addToCart(customerId, productId, quantity);
        return ResponseEntity.ok(updatedCart.getBody());
    }

    @Operation(
            summary = "Remove a product from the shopping cart",
            description = "Removes a specified product from the customer's shopping cart"
    )
    @DeleteMapping("{customerId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public ResponseEntity<Cart> removeFromCart(
            @Parameter(description = "Customer ID from which the product will be removed", required = true)
            @PathVariable UUID customerId,

            @Parameter(description = "Product ID to remove from the shopping cart", required = true)
            @RequestParam UUID productId) {

        Cart updatedCart = cartService.removeItemFromCart(customerId, productId);
        return ResponseEntity.ok(updatedCart);
    }
}
