package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.clients.CustomerClient;
import br.com.loomi.ordermicroservice.clients.ProductClient;
import br.com.loomi.ordermicroservice.exceptions.BadRequestException;
import br.com.loomi.ordermicroservice.exceptions.NotFoundException;
import br.com.loomi.ordermicroservice.models.dtos.ProductDto;
import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CART_KEY_PREFIX = "cart:";
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    public CartService(ProductClient productClient, CustomerClient customerClient, RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.productClient = productClient;
        this.customerClient = customerClient;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    private String buildRedisKey(UUID customerId) {
        return CART_KEY_PREFIX + customerId.toString();
    }

    public void saveCartToRedis(UUID customerId, Cart cart) {
        try {
            String cartJson = objectMapper.writeValueAsString(cart);
            String redisKey = buildRedisKey(customerId);

            redisTemplate.opsForValue().set(redisKey, cartJson, 5, TimeUnit.HOURS);
            logger.info("Saved cart to Redis: key={}, value={}", redisKey, cartJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize cart object to JSON");
        }
    }

    public Cart getCartFromRedis(UUID customerId) {
        try {
            String redisKey = buildRedisKey(customerId);
            String cartJson = redisTemplate.opsForValue().get(redisKey);

            if (cartJson != null) {
                logger.info("Retrieved cart from Redis: key={}, value={}", redisKey, cartJson);
                return objectMapper.readValue(cartJson, Cart.class);
            }

            logger.warn("No items found in cart");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize cart JSON to object");
        }
        return null;
    }

    public ResponseEntity<Cart> addToCart(UUID customerId, UUID productId, Integer quantity) {
        try {
            ProductDto product = productClient.findById(productId).getBody();
            customerClient.findUserEmailByCustomerId(customerId).getBody();

            if (product.getQtd() < quantity) {
                logger.warn("Insufficient stock for productId={} requested quantity={}", productId, quantity);
                throw new BadRequestException("Insufficient quantity in stock");
            }

            Cart cart = getCart(customerId);
            cart.setCustomerId(customerId);

            CartItem item = new CartItem();
            item.setProductId(productId);
            item.setName(product.getName());
            item.setQtd(quantity);
            item.setPricePerUnit(product.getPrice());

            cart.addItem(item);

            saveCartToRedis(customerId, cart);
            return ResponseEntity.ok(cart);
        } catch (FeignException e) {
            String content = e.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            logger.error("Failed to fetch product details from ProductClient: {}", message);
            throw new NotFoundException(message);
        }
    }

    public Cart getCart(UUID customerId) {
        try {
            customerClient.findUserEmailByCustomerId(customerId).getBody();

            Cart cart = getCartFromRedis(customerId);
            if (cart != null) {
                return cart;
            }

            return new Cart();
        } catch (FeignException e) {
            String content = e.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            throw new NotFoundException(message);
        }
    }

    public Cart removeItemFromCart(UUID customerId, UUID productId) {
        try {
            customerClient.findUserEmailByCustomerId(customerId).getBody();
            productClient.findById(productId).getBody();

            Cart cart = getCart(customerId);

            if (cart != null) {
                cart.removeItem(productId);
                if (cart.getItems().isEmpty()) {
                    redisTemplate.delete(buildRedisKey(customerId));
                } else {
                    saveCartToRedis(customerId, cart);
                }
            }
            return cart;
        } catch (FeignException e) {
            String content = e.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            throw new NotFoundException(message);
        }
    }
}
