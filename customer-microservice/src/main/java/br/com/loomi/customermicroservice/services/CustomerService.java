package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.exceptions.BadRequestException;
import br.com.loomi.customermicroservice.exceptions.NotFoundException;
import br.com.loomi.customermicroservice.exceptions.UnauthorizedException;
import br.com.loomi.customermicroservice.models.dtos.CustomerDto;
import br.com.loomi.customermicroservice.models.dtos.UpdateCustomerDto;
import br.com.loomi.customermicroservice.models.entities.Customer;
import br.com.loomi.customermicroservice.models.entities.User;
import br.com.loomi.customermicroservice.models.enums.UserType;
import br.com.loomi.customermicroservice.repositories.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerService {

    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private MailService mailService;

    public CustomerService(MailService mailService, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Transactional
    public Customer save(CustomerDto customerDto) {
        boolean emailExists = customerRepository.existsByUserEmail(customerDto.getUserDto().getEmail());

        if (emailExists) {
            throw new BadRequestException("The e-mail provided is already in use");
        }

        User user = new User();
        BeanUtils.copyProperties(customerDto.getUserDto(), user);

        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDto, customer);
        customer.setUser(user);
        customer.setStatus(false);
        customer.getUser().setPassword(passwordEncoder.encode(customerDto.getUserDto().getPassword()));

        Customer customerSaved = customerRepository.save(customer);

        if (customerSaved != null) {
            this.mailService.sendConfirmationEmailAsync(user);
        }

        return customerSaved;
    }

    @Transactional
    public void update(UUID id, UpdateCustomerDto updateCustomerDto) {
        Customer customer = this.getLoggedUser();

        if (customer.getUser().getType() == UserType.CUSTOMER && !customer.getId().equals(id)) {
            throw new AccessDeniedException("You do not have permission to edit another user's data");
        }

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
                        if (updateCustomerDto.getUserDto().getPassword() != null) {
                            c.getUser().setPassword(passwordEncoder.encode(updateCustomerDto.getUserDto().getPassword()));
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
        Customer customer = this.getLoggedUser();

        if (customer.getUser().getType() == UserType.CUSTOMER && !customer.getId().equals(id)) {
            throw new AccessDeniedException("You do not have permission to delete another user");
        }

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

        return customers.map(customer -> {
            customer.getUser().setPassword(null);
            return customer;
        });

    }

    public Customer loadUserByEmail(String email) {
        return this.customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public Customer findByIdWithUser(UUID id) {
        return this.customerRepository.findByIdWithUser(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public Customer getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            String username = null;

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            return this.loadUserByEmail(username);
        }

        throw new UnauthorizedException("Unauthenticated user");
    }
}
