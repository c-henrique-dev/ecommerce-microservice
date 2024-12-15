package br.com.loomi.authmicroservice.services;

import br.com.loomi.authmicroservice.models.enums.UserType;
import br.com.loomi.authmicroservice.models.Customer;
import br.com.loomi.authmicroservice.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private String expiration;
    private String keySignature;

    @BeforeEach
    public void setUp() {
        expiration = "60";
        keySignature = "test-key";

        jwtService = new JwtService();
        jwtService.expiration = expiration;
        jwtService.keySignature = keySignature;
    }

    @Test
    public void createToken_WithValidData_ReturnsToken() {
        User user = User.builder()
                .email("carlossoaressantana@hotmail.com")
                .type(UserType.CUSTOMER)
                .name("teste")
                .build();

        Customer customer = Customer.builder()
                .fullName("Carlos Henrique")
                .contact("81996589021")
                .address("dsfdgsdgsdf")
                .status(false)
                .user(user)
                .build();

        String token = jwtService.getToken(customer);

        assertThat(token).isInstanceOf(String.class);
        assertThat(token).isNotEmpty();
    }
}
