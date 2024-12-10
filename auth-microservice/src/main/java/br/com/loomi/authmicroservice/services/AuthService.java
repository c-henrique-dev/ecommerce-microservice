package br.com.loomi.authmicroservice.services;

import br.com.loomi.authmicroservice.clients.CustomerClient;
import br.com.loomi.authmicroservice.exceptions.BadRequestException;
import br.com.loomi.authmicroservice.exceptions.NotFoundException;
import br.com.loomi.authmicroservice.models.Customer;
import br.com.loomi.authmicroservice.models.User;
import br.com.loomi.authmicroservice.models.dtos.AuthDto;
import br.com.loomi.authmicroservice.models.dtos.TokenDto;
import br.com.loomi.authmicroservice.models.dtos.UserDto;
import br.com.loomi.authmicroservice.security.CustomUserDetailsService;
import feign.FeignException;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class AuthService {

    private CustomerClient customerClient;
    private CustomUserDetailsService customUserDetailsService;
    private JwtService jwtService;

    public AuthService(CustomerClient customerClient, CustomUserDetailsService customUserDetailsService, JwtService jwtService) {
        this.customerClient = customerClient;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    public Customer loadUserByEmail(String email) {
        try {
            return this.customerClient.loadByEmail(email).getBody();
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public String validToken(@PathVariable String token) {
        return jwtService.validToken(token);
    }


    public UserDetails loadUserDetailByEmail(String email) {
        return this.customUserDetailsService.loadUserByUsername(email);
    }

    public ResponseEntity auth(AuthDto authDto) {
        try {
            UserDto userDto = new UserDto();
            User user = User.builder()
                    .email(authDto.getEmail())
                    .password(authDto.getPassword()).build();
            BeanUtils.copyProperties(user, userDto);
            Customer customer = this.customerClient.loadByEmail(authDto.getEmail()).getBody();

            if (customer.getStatus() == false) {
                throw new BadRequestException("e-mail not validated");
            }

            this.customUserDetailsService.auth(userDto);
            String token = jwtService.getToken(customer);
            return ResponseEntity.ok(new TokenDto(token));
        } catch (FeignException e) {
            String content = e.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            throw new NotFoundException(message);
        }
    }
}
