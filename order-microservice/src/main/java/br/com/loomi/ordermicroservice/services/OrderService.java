package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.clients.CustomerClient;
import br.com.loomi.ordermicroservice.clients.ProductClient;
import br.com.loomi.ordermicroservice.exceptions.NotFoundException;
import br.com.loomi.ordermicroservice.exceptions.UnauthorizedException;
import br.com.loomi.ordermicroservice.models.dtos.CustomerDto;
import br.com.loomi.ordermicroservice.models.dtos.OrderWithProductDTO;
import br.com.loomi.ordermicroservice.models.dtos.PaymentDto;
import br.com.loomi.ordermicroservice.models.dtos.ProductDto;
import br.com.loomi.ordermicroservice.models.entities.Cart;
import br.com.loomi.ordermicroservice.models.entities.Order;
import br.com.loomi.ordermicroservice.models.entities.OrderItem;
import br.com.loomi.ordermicroservice.models.enums.OrderStatus;
import br.com.loomi.ordermicroservice.models.enums.UserType;
import br.com.loomi.ordermicroservice.queues.PaymentPublisher;
import br.com.loomi.ordermicroservice.repositories.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ProductClient productClient;
    private CustomerClient customerClient;
    private CartService cartService;
    private PaymentPublisher paymentPublisher;

    public OrderService(ProductClient productClient, CustomerClient customerClient, OrderRepository orderRepository, CartService cartService, PaymentPublisher paymentPublisher) {
        this.orderRepository = orderRepository;
        this.customerClient = customerClient;
        this.productClient = productClient;
        this.cartService = cartService;
        this.paymentPublisher = paymentPublisher;
    }

    @Transactional
    public Order createOrderFromCart(Cart cart) {
        Order order = Order.builder()
                .customerId(cart.getCustomerId())
                .totalOfOrder(cart.getTotal())
                .orderStatus(OrderStatus.AWAITING_PAYMENT)
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

        Order orderSaved = orderRepository.save(order);

        if (orderSaved.getId() != null) {
            cart.getItems().forEach(item ->
                    cartService.removeItemFromCart(cart.getCustomerId(), item.getProductId())
            );

        }

        PaymentDto paymentDto = PaymentDto.builder()
                .customerId(order.getCustomerId())
                .orderId(order.getId())
                .transactionId(UUID.randomUUID())
                .amount(order.getTotalOfOrder())
                .build();

        this.paymentPublisher.makePayment(paymentDto);

        return orderSaved;
    }

    public Order findById(UUID id) {
        return this.orderRepository
                .findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Order not found"));
    }

    public List<OrderWithProductDTO> findOrdersByPeriod(LocalDateTime initialDate, LocalDateTime endDate) {
        List<Object[]> orders = orderRepository.findOrdersByPeriod(initialDate, endDate);

        if (orders.isEmpty()) {
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

    @Transactional
    public void updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update status of a delivered order.");
        }

        order.setOrderStatus(newStatus);
        this.orderRepository.save(order);
    }

    public Page<Order> find(Pageable pageable) throws Exception {
        CustomerDto customer = this.getLoggedUser();

        if (customer.getUser().getType() == UserType.CUSTOMER) {
            return orderRepository.findByCustomerId(customer.getId(), pageable);
        }

        if (customer.getUser().getType() == UserType.ADMIN) {
            return orderRepository.findAll(pageable);
        }

        Page<Order> orders = orderRepository.findAll(pageable);
        return orders;
    }

    public CustomerDto getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String username = null;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            return this.customerClient.loadByEmail(username).getBody();
        }

        throw new UnauthorizedException("Unauthenticated user");
    }
}
