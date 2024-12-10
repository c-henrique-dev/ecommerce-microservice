package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.clients.ProductClient;
import br.com.loomi.ordermicroservice.exceptions.BadRequestException;
import br.com.loomi.ordermicroservice.exceptions.NotFoundException;
import br.com.loomi.ordermicroservice.models.dtos.ProductDto;
import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.CartItem;
import feign.FeignException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CartService {

    private final ProductClient productClient;


    private final Map<UUID, Cart> carts = new HashMap<>();

    public CartService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public void setCarts(UUID customerId, Cart cart) {
        carts.put(customerId, cart);
    }

    public Cart getCart(UUID customerId) {
        return carts.getOrDefault(customerId, new Cart());
    }

    public ResponseEntity<Cart> addToCart(UUID customerId, UUID productId, Integer quantity) {
        try {
            ProductDto product = productClient.findById(productId).getBody();

            if (product.getQtd() < quantity) {
                throw new BadRequestException("Insufficient quantity in stock");
            }

            Cart cart = carts.getOrDefault(customerId, new Cart());
            cart.setCustomerId(customerId);

            CartItem item = new CartItem();
            item.setProductId(productId);
            item.setName(product.getName());
            item.setQtd(quantity);
            item.setPricePerUnit(product.getPrice());

            cart.addItem(item);
            carts.put(customerId, cart);

            return ResponseEntity.ok(cart);
        } catch (FeignException e) {
            String content = e.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            throw new NotFoundException(message);
        }
    }

    public Cart removeFromCart(UUID customerId, UUID productId) {
        Cart cart = carts.get(customerId);
        if (cart != null) {
            cart.removeItem(productId);
        }
        return cart;
    }
}
