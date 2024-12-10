package br.com.loomi.ordermicroservice.configs;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    @Value("${mq.payment.queue}")
    private String paymentQueue;

    @Bean
    public Queue paymentQueue() {
        return new Queue(paymentQueue, true);
    }
}