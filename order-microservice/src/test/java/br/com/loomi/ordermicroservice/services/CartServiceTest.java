package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.clients.ProductClient;
import br.com.loomi.ordermicroservice.exceptions.BadRequestException;
import br.com.loomi.ordermicroservice.models.dtos.ProductDto;
import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.CartItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private ProductClient productClient;

    @Mock
    private RedisTemplate<String, Cart> redisTemplate;

    @Test
    public void addToCart_WhenProductIsAvailable_ShouldAddItemToCart() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 2;

        ProductDto productDto = ProductDto.builder()
                .id(productId)
                .name("Playstation 5")
                .qtd(5)
                .price(new BigDecimal("5200"))
                .build();

        String redisKey = "cart:" + customerId;

        when(productClient.findById(productId)).thenReturn(ResponseEntity.ok(productDto));

        RedisTemplate<String, String> redisTemplateMock = mock(RedisTemplate.class);
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);
        when(valueOperationsMock.get(redisKey)).thenReturn(null);

        CartService cartService = new CartService(productClient, redisTemplateMock, new ObjectMapper());

        ResponseEntity<Cart> response = cartService.addToCart(customerId, productId, quantity);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCustomerId()).isEqualTo(customerId);
        assertThat(response.getBody().getItems()).hasSize(1);
        assertThat(response.getBody().getItems().get(0).getProductId()).isEqualTo(productId);
        assertThat(response.getBody().getItems().get(0).getQtd()).isEqualTo(quantity);

        verify(productClient).findById(productId);
        verify(valueOperationsMock).get(redisKey);
    }

    @Test
    public void addToCart_WhenQuantityIsInsufficient_ShouldThrowBadRequestException() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer requestedQuantity = 5;

        ProductDto productDto = ProductDto.builder()
                .id(productId)
                .name("Playstation 5")
                .qtd(3)
                .price(new BigDecimal("5200"))
                .build();

        when(productClient.findById(productId)).thenReturn(ResponseEntity.ok(productDto));

        assertThatThrownBy(() -> cartService.addToCart(customerId, productId, requestedQuantity))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Insufficient quantity in stock");
    }

    @Test
    public void removeFromCart_WhenCartAndItemExist_ShouldRemoveItem() throws JsonProcessingException {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CartItem cartItem = CartItem.builder()
                .productId(productId)
                .name("Playstation 5")
                .qtd(2)
                .pricePerUnit(new BigDecimal("5200"))
                .build();

        Cart cart = Cart.builder()
                .customerId(customerId)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();

        RedisTemplate<String, String> redisTemplateMock = mock(RedisTemplate.class);
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);

        CartService cartService = new CartService(productClient, redisTemplateMock, new ObjectMapper());

        Cart response = cartService.removeItemFromCart(customerId, productId);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    public void removeFromCart_WhenCartIsEmpty_ShouldReturnEmptyCart() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        RedisTemplate<String, String> redisTemplateMock = mock(RedisTemplate.class);
        ValueOperations<String, String> valueOperationsMock = mock(ValueOperations.class);
        when(redisTemplateMock.opsForValue()).thenReturn(valueOperationsMock);

        String redisKey = "cart:" + customerId;
        when(valueOperationsMock.get(redisKey)).thenReturn(null);

        CartService cartService = new CartService(productClient, redisTemplateMock, new ObjectMapper());
        Cart response = cartService.removeItemFromCart(customerId, productId);

        assertThat(response).isNotNull();
        assertThat(response.getItems()).isEmpty();
    }
}
