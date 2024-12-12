package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.CartItem;
import br.com.loomi.ordermicroservice.models.entities.Order;
import br.com.loomi.ordermicroservice.models.entities.OrderItem;
import br.com.loomi.ordermicroservice.models.enums.OrderStatus;
import br.com.loomi.ordermicroservice.queues.PaymentPublisher;
import br.com.loomi.ordermicroservice.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private PaymentPublisher paymentPublisher;

    @Test
    public void createOrderFromCart_ReturnsSavedOrder() {
        Cart cart = Cart.builder()
                .customerId(UUID.randomUUID())
                .items(List.of(
                        CartItem.builder()
                                .productId(UUID.randomUUID())
                                .qtd(2)
                                .pricePerUnit(BigDecimal.valueOf(500))
                                .build(),
                        CartItem.builder()
                                .productId(UUID.randomUUID())
                                .qtd(1)
                                .pricePerUnit(BigDecimal.valueOf(500))
                                .build()
                ))
                .build();

        Order savedOrder = Order.builder()
                .id(UUID.randomUUID())
                .customerId(cart.getCustomerId())
                .totalOfOrder(cart.getTotal())
                .orderStatus(OrderStatus.INPREPARATION)
                .orderItems(cart.getItems().stream().map(item ->
                        OrderItem.builder()
                                .productId(item.getProductId())
                                .qtd(item.getQtd())
                                .pricePerUnit(item.getPricePerUnit())
                                .build()
                ).toList())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrderFromCart(cart);

        assertThat(result).isNotNull();
        assertThat(result.getCustomerId()).isEqualTo(cart.getCustomerId());
        assertThat(result.getTotalOfOrder()).isEqualTo(cart.getTotal());
        assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.INPREPARATION);

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void updateOrderStatus_WhenOrderExists_AndValidTransition_ShouldUpdateStatus() {
        UUID orderId = UUID.randomUUID();
        Order existingOrder = Order.builder()
                .id(orderId)
                .orderStatus(OrderStatus.INPREPARATION)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        orderService.updateOrderStatus(orderId, OrderStatus.RECEIVED);

        assertThat(existingOrder.getOrderStatus()).isEqualTo(OrderStatus.RECEIVED);

        verify(orderRepository).save(existingOrder);
    }
}
