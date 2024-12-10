package br.com.loomi.authmicroservice.security;

import br.com.loomi.authmicroservice.clients.CustomerClient;
import br.com.loomi.authmicroservice.exceptions.NotFoundException;
import br.com.loomi.authmicroservice.models.Customer;
import br.com.loomi.authmicroservice.models.User;
import br.com.loomi.authmicroservice.models.dtos.UserDto;
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
    private CustomerClient customerClient;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerClient.loadByEmail(email).getBody();
        if (customer == null || customer.getUser() == null) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = customer.getUser();

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getType().name())
                .build();
    }

    public UserDetails auth(UserDto userDto) {
        UserDetails user = loadUserByUsername(userDto.getEmail());
        boolean passwordMatch = passwordEncoder.matches(userDto.getPassword(), user.getPassword());
        if (passwordMatch) {
            return user;
        } else {
            throw new NotFoundException("User not found.");
        }
    }
}
