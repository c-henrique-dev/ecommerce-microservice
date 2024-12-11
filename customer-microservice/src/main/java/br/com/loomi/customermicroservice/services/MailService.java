package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.models.entities.Customer;
import br.com.loomi.customermicroservice.models.entities.User;
import br.com.loomi.customermicroservice.repositories.CustomerRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MailService {

    private JavaMailSender javaMailSender;
    private CustomerRepository customerRepository;

    public MailService(JavaMailSender javaMailSender, CustomerRepository customerRepository) {
        this.javaMailSender = javaMailSender;
        this.customerRepository = customerRepository;
    }

    @Async
    public void sendConfirmationEmailAsync(User user) {
        try {
            String confirmationLink = "http://localhost:8888/customer/mail/confirm?email=" + user.getEmail();

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo("carlossoaressantana081@gmail.com");
            helper.setSubject("Confirme seu e-mail");
            helper.setText("<p>Clique no link para confirmar seu e-mail: <a href=\"" + confirmationLink + "\">Clique aqui para confirmar</a></p>", true);

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String confirmEmail(String email) {
        Optional<Customer> userOpt = customerRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            Customer customer = userOpt.get();
            customer.setStatus(true);
            customerRepository.save(customer);
            return "E-mail confirmado com sucesso";
        }
        return "Erro ao confirmar o e-mail";
    }
}
