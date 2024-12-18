package br.com.loomi.authmicroservice.services;

import br.com.loomi.authmicroservice.clients.CustomerClient;
import br.com.loomi.authmicroservice.models.dtos.AuthDto;
import br.com.loomi.authmicroservice.models.dtos.CustomerDto;
import br.com.loomi.authmicroservice.models.dtos.TokenDto;
import br.com.loomi.authmicroservice.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void should_Authenticate_And_Return_Token() {
        AuthDto authDto = new AuthDto("test@example.com", "password");
        CustomerDto customerDto = CustomerDto.builder().id(UUID.randomUUID()).build();
        String token = "generatedToken";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(customerClient.loadByEmail(authDto.getEmail())).thenReturn(ResponseEntity.ok(customerDto));
        when(jwtService.getToken(any())).thenReturn(token);
        when(valueOperations.get(authDto.getEmail())).thenReturn(null);

        ResponseEntity<TokenDto> response = authService.auth(authDto);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(token, response.getBody().getToken());
    }
}
