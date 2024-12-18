package br.com.loomi.paymentmicroservice.services;

import br.com.loomi.paymentmicroservice.clients.CustomerClient;
import br.com.loomi.paymentmicroservice.models.dtos.CustomerDto;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MailService {

    private JavaMailSender javaMailSender;
    private CustomerClient customerClient;

    public MailService(JavaMailSender javaMailSender, CustomerClient customerClient) {
        this.javaMailSender = javaMailSender;
        this.customerClient = customerClient;
    }

    @Async
    public void sendPaymentEmailAsync(UUID clientId, String subject, String text) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            CustomerDto customer = this.customerClient.findUserEmailByCustomerId(clientId).getBody();
            helper.setTo(customer.getUser().getEmail());
            helper.setSubject(subject);
            helper.setText(text);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
