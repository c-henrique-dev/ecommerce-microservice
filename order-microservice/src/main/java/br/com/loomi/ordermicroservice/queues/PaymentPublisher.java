package br.com.loomi.ordermicroservice.queues;

import br.com.loomi.ordermicroservice.models.dtos.PaymentDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PaymentPublisher {
    private RabbitTemplate rabbitTemplate;
    private Queue paymentQueue;

    public PaymentPublisher(RabbitTemplate rabbitTemplate, Queue paymentQueue) {
        this.rabbitTemplate = rabbitTemplate;
        this.paymentQueue = paymentQueue;
    }

    public PaymentDto makePayment(PaymentDto paymentDto) {
        var json = convertIntoJson(paymentDto);
        rabbitTemplate.convertAndSend(paymentQueue.getName(), json);
        return paymentDto;
    }

    private String convertIntoJson(PaymentDto paymentDto) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        try {
            json = mapper.writeValueAsString(paymentDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }
}