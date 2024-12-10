package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.exceptions.BadRequestException;
import br.com.loomi.customermicroservice.exceptions.NotFoundException;
import br.com.loomi.customermicroservice.models.dtos.CustomerDto;
import br.com.loomi.customermicroservice.models.dtos.UpdateCustomerDto;
import br.com.loomi.customermicroservice.models.entities.Customer;
import br.com.loomi.customermicroservice.models.entities.User;
import br.com.loomi.customermicroservice.repositories.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private JavaMailSender javaMailSender;

    public CustomerService(JavaMailSender javaMailSender, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
    }

    @Transactional
    public Customer save(CustomerDto customerDto) {
        try {
            User user = new User();
            BeanUtils.copyProperties(customerDto.getUserDto(), user);

            Customer customer = new Customer();
            BeanUtils.copyProperties(customerDto, customer);
            customer.setUser(user);
            customer.setStatus(false);
            customer.getUser().setPassword(passwordEncoder.encode(customerDto.getUserDto().getPassword()));

            sendConfirmationEmailAsync(user);

            Customer customerSaved = customerRepository.save(customer);

            return customerSaved;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Async
    public void sendConfirmationEmailAsync(User user) {
        try {
            String confirmationLink = "http://localhost:8888/customer/confirm?email=" + user.getEmail();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("carlossoaressantana081@gmail.com");
            message.setSubject("Confirme seu e-mail");
            message.setText("Clique no link para confirmar seu e-mail: " + confirmationLink);

            javaMailSender.send(message);
        } catch (Exception e) {
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

    @Transactional
    public void update(UUID id, UpdateCustomerDto updateCustomerDto) {
        this.customerRepository.findById(id)
                .map(c -> {
                    if (updateCustomerDto.getFullName() != null) {
                        c.setFullName(updateCustomerDto.getFullName());
                    }

                    if (updateCustomerDto.getAddress() != null) {
                        c.setAddress(updateCustomerDto.getAddress());
                    }

                    if (updateCustomerDto.getContact() != null) {
                        c.setContact(updateCustomerDto.getContact());
                    }

                    if (updateCustomerDto.getUserDto() != null) {
                        if (updateCustomerDto.getUserDto().getEmail() != null) {
                            c.getUser().setEmail(updateCustomerDto.getUserDto().getEmail());
                        }
                        if (updateCustomerDto.getUserDto().getPassword() != null) {
                            c.getUser().setPassword(passwordEncoder.encode(updateCustomerDto.getUserDto().getPassword()));
                        }

                        if (updateCustomerDto.getUserDto().getName() != null) {
                            c.getUser().setName(updateCustomerDto.getUserDto().getName());
                        }

                        if (updateCustomerDto.getUserDto().getType() != null) {
                            c.getUser().setType(updateCustomerDto.getUserDto().getType());
                        }
                    }
                    customerRepository.save(c);
                    return c;
                }).orElseThrow(() -> new NotFoundException("Customer not found"));
    }


    @Transactional
    public void delete(UUID id) {
        this.customerRepository
                .findById(id)
                .map(p -> {
                    customerRepository.delete(p);
                    return Void.TYPE;
                }).orElseThrow(() ->
                        new NotFoundException("Customer not found"));
    }

    public Page<Customer> find(Customer filter, Pageable pageable) throws Exception {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(
                        ExampleMatcher.StringMatcher.CONTAINING);

        Example<Customer> example = Example.of(filter, matcher);
        Page<Customer> customers = customerRepository.findAll(example, pageable);

        return customers;
    }

    public Customer loadUserByEmail(String email) {
        return this.customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
