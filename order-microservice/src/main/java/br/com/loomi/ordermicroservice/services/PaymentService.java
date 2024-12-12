package br.com.loomi.ordermicroservice.services;

import br.com.loomi.ordermicroservice.clients.ProductClient;
import br.com.loomi.ordermicroservice.exceptions.NotFoundException;
import br.com.loomi.ordermicroservice.models.dtos.PaymentDto;
import br.com.loomi.ordermicroservice.models.entities.Order;
import br.com.loomi.ordermicroservice.models.enums.OrderStatus;
import br.com.loomi.ordermicroservice.models.enums.PaymentStatus;
import br.com.loomi.ordermicroservice.queues.PaymentPublisher;
import br.com.loomi.ordermicroservice.repositories.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {
    private OrderRepository orderRepository;
    private PaymentPublisher paymentPublisher;
    private ProductClient productClient;

    public PaymentService(OrderRepository orderRepository, PaymentPublisher paymentPublisher, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.paymentPublisher = paymentPublisher;
        this.productClient = productClient;
    }

    public ResponseEntity<Map> simulatePayment(UUID orderId) throws JsonProcessingException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        boolean paymentSuccessful = new Random().nextBoolean();

        if (paymentSuccessful) {
            order.setOrderStatus(OrderStatus.RECEIVED);
            orderRepository.save(order);
            PaymentDto paymentDto = PaymentDto.builder()
                    .customerId(order.getCustomerId())
                    .orderId(orderId)
                    .status(PaymentStatus.APPROVED)
                    .transactionId(UUID.randomUUID())
                    .amount(order.getTotalOfOrder())
                    .build();

            this.paymentPublisher.makePayment(paymentDto);
            try {
                order.getOrderItems().forEach(item -> {
                    productClient.debitStock(item.getProductId(), item.getQtd());
                });
            } catch (FeignException e) {
                String content = e.contentUTF8();
                String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
                throw new NotFoundException(message);
            }

            return ResponseEntity.ok(Map.of("message", "Payment confirmed. Stock debited and order status updated to RECEIVED."));
        } else {
            PaymentDto paymentDto = PaymentDto.builder()
                    .customerId(order.getCustomerId())
                    .orderId(orderId)
                    .status(PaymentStatus.REFUSED)
                    .transactionId(UUID.randomUUID())
                    .amount(order.getTotalOfOrder())
                    .build();

            this.paymentPublisher.makePayment(paymentDto);
            order.setOrderStatus(OrderStatus.PAYMENT_REJECTED);
            orderRepository.save(order);

            return ResponseEntity.ok(Map.of("message", "Payment denied. Order status updated to PAYMENT_REJECTED."));
        }
    }
}
