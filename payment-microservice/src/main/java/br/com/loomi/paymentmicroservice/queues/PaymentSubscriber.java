package br.com.loomi.paymentmicroservice.queues;

import br.com.loomi.paymentmicroservice.models.Payment;
import br.com.loomi.paymentmicroservice.repositories.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentSubscriber {
    private PaymentRepository paymentRepository;

    public PaymentSubscriber(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @RabbitListener(queues = "${mq.payment.queue}")
    public void receivePayment(@Payload String payload) {
        try {
            var mapper = new ObjectMapper();
            Payment payment = mapper.readValue(payload, Payment.class);
            payment.setStatus();
            this.paymentRepository.save(payment);
            log.info("Recebi a mensagem");
        } catch (Exception e) {
            log.error("Erro ao receber solicitação de emissão de cartão: {} ", e.getMessage());
        }
    }
}