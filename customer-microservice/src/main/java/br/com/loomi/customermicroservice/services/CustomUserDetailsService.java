package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.exceptions.NotFoundException;
import br.com.loomi.customermicroservice.models.entities.Customer;
import br.com.loomi.customermicroservice.models.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerService.loadUserByEmail(email);
        if (customer == null || customer.getUser() == null) {
            throw new NotFoundException("User not found");
        }

        User user = customer.getUser();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getType().name())
                .build();
    }
}
