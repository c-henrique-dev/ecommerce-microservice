package br.com.loomi.paymentmicroservice.queues;

import br.com.loomi.paymentmicroservice.models.entities.Payment;
import br.com.loomi.paymentmicroservice.models.enums.PaymentStatus;
import br.com.loomi.paymentmicroservice.repositories.PaymentRepository;
import br.com.loomi.paymentmicroservice.services.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PaymentSubscriber {
    private PaymentRepository paymentRepository;
    private MailService mailService;

    public PaymentSubscriber(PaymentRepository paymentRepository, MailService mailService) {
        this.paymentRepository = paymentRepository;
        this.mailService = mailService;
    }

    @RabbitListener(queues = "${mq.payment.queue}")
    public void receivePayment(@Payload String payload) {
        try {
            var mapper = new ObjectMapper();
            Payment payment = mapper.readValue(payload, Payment.class);
            Payment paymentSaved = this.paymentRepository.save(payment);

            if (paymentSaved.getStatus() != PaymentStatus.APPROVED) {
                this.mailService.sendPaymentEmailAsync(paymentSaved.getCustomerId(),"Seu pagamento foi recusado! :(",
                        "Informamos que a sua tentativa de pagamento foi recusada. " +
                                "Solicitamos que verifique os dados do seu cartão, confirme se possui saldo ou " +
                                "limite disponível e entre em contato com sua instituição financeira para " +
                                "identificar qualquer bloqueio.");
            } else {
                this.mailService.sendPaymentEmailAsync(paymentSaved.getCustomerId(),"Seu pagamento foi aprovado! :)",
                        "É com satisfação que informamos que o seu pagamento foi aprovado com sucesso." +
                                " Agradecemos a confirmação e o processamento foi concluído normalmente.");
            }

            log.info("Message coming from RabbitMQ");
        } catch (Exception e) {
            log.error("Error receiving request: {} ", e.getMessage());
        }
    }
}