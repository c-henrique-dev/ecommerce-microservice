package br.com.loomi.authmicroservice.services;

import br.com.loomi.authmicroservice.clients.CustomerClient;
import br.com.loomi.authmicroservice.exceptions.BadRequestException;
import br.com.loomi.authmicroservice.exceptions.NotFoundException;
import br.com.loomi.authmicroservice.models.dtos.CustomerDto;
import br.com.loomi.authmicroservice.models.dtos.AuthDto;
import br.com.loomi.authmicroservice.models.dtos.TokenDto;
import br.com.loomi.authmicroservice.models.dtos.UserDto;
import br.com.loomi.authmicroservice.security.CustomUserDetailsService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
public class AuthService {

    private CustomerClient customerClient;
    private CustomUserDetailsService customUserDetailsService;
    private JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthService(CustomerClient customerClient, CustomUserDetailsService customUserDetailsService,
                       JwtService jwtService, RedisTemplate<String, String> redisTemplate) {
        this.customerClient = customerClient;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    public ResponseEntity auth(AuthDto authDto) {
        try {
            UserDto userDto = new br.com.loomi.authmicroservice.models.dtos.UserDto();
            UserDto user = UserDto.builder()
                    .email(authDto.getEmail())
                    .password(authDto.getPassword()).build();
            BeanUtils.copyProperties(user, userDto);

            CustomerDto customer = this.customerClient.loadByEmail(authDto.getEmail()).getBody();

            if (Boolean.FALSE.equals(customer.getStatus())) {
                throw new BadRequestException("E-mail not validated");
            }

            this.customUserDetailsService.auth(userDto);

            String cachedToken = redisTemplate.opsForValue().get(authDto.getEmail());
            if (cachedToken != null) {
                log.info("getting token from redis");
                return ResponseEntity.ok(new TokenDto(cachedToken));
            }

            String token = jwtService.getToken(customer);

            redisTemplate.opsForValue().set(authDto.getEmail(), token, Duration.ofHours(1));

            return ResponseEntity.ok(new TokenDto(token));
        } catch (FeignException e) {
            String content = e.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            throw new NotFoundException(message);
        }
    }
}
