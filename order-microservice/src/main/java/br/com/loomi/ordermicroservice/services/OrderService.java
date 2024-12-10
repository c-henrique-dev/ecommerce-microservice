package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.clients.ProductClient;
import br.com.loomi.ordermicroservice.exceptions.NotFoundException;
import br.com.loomi.ordermicroservice.models.dtos.OrderWithProductDTO;
import br.com.loomi.ordermicroservice.models.dtos.ProductDto;
import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.Order;
import br.com.loomi.ordermicroservice.models.entities.OrderItem;
import br.com.loomi.ordermicroservice.models.enums.OrderStatus;
import br.com.loomi.ordermicroservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ProductClient productClient;

    public OrderService(ProductClient productClient, OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
    }

    public Order createOrderFromCart(Cart cart) {
        Order order = Order.builder()
                .customerId(cart.getCustomerId())
                .totalOfOrder(cart.getTotal())
                .orderStatus(OrderStatus.INPREPARATION)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream().map(item ->
                OrderItem.builder()
                        .productId(item.getProductId())
                        .qtd(item.getQtd())
                        .pricePerUnit(item.getPricePerUnit())
                        .order(order)
                        .build()
        ).toList();

        order.setOrderItems(orderItems);

        return orderRepository.save(order);
    }

    public Order findById(UUID id) {
        return this.orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Order not found"));
    }

    public List<OrderWithProductDTO> findOrdersByPeriod(LocalDateTime initialDate, LocalDateTime endDate) {
        List<Object[]> orders = orderRepository.findOrdersByPeriod(initialDate, endDate);

        if(orders.isEmpty()) {
            throw new NotFoundException("Orders not found");
        }

        List<OrderWithProductDTO> orderWithProductDTOs = new ArrayList<>();

        for (Object[] order : orders) {
            UUID productId = (UUID) order[0];
            Long totalQtdLong = (Long) order[1];
            BigDecimal totalValor = (BigDecimal) order[2];

            Integer totalQtd = totalQtdLong.intValue();
            ProductDto product = productClient.findById(productId).getBody();

            OrderWithProductDTO orderWithProductDTO = new OrderWithProductDTO(
                    productId,
                    product.getName(),
                    product.getPrice(),
                    totalQtd,
                    totalValor
            );
            orderWithProductDTOs.add(orderWithProductDTO);
        }

        return orderWithProductDTOs;
    }

    public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update status of a delivered order.");
        }

        validateStatusTransition(order.getOrderStatus(), newStatus);

        order.setOrderStatus(newStatus);
        this.orderRepository.save(order);
    }

    private void validateStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        switch (currentStatus) {
            case INPREPARATION:
                if (newStatus != OrderStatus.RECEIVED) {
                    throw new IllegalStateException("Invalid status transition from PREPARATION to " + newStatus);
                }
                break;
            case RECEIVED:
                if (newStatus != OrderStatus.DISPATCHED) {
                    throw new IllegalStateException("Invalid status transition from RECEIVED to " + newStatus);
                }
                break;
            case DISPATCHED:
                if (newStatus != OrderStatus.DELIVERED) {
                    throw new IllegalStateException("Invalid status transition from DISPATCHED to " + newStatus);
                }
                break;
            default:
                throw new IllegalStateException("No transitions allowed from status: " + currentStatus);
        }
    }
}
