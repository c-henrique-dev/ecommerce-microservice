package br.com.loomi.authmicroservice.services;

import br.com.loomi.authmicroservice.clients.CustomerClient;
import br.com.loomi.authmicroservice.models.enums.UserType;
import br.com.loomi.authmicroservice.models.Customer;
import br.com.loomi.authmicroservice.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService  authService;

    @Mock
    private CustomerClient customerClient;

    @Test
    public void createPlanet_WithValidData_ReturnsPlanet() {
        User user = User.builder().email("carlossoaressantana@hotmail.com").type(UserType.CUSTOMER).name("teste").build();
        Customer customer = Customer.builder().fullName("Carlos Henrique").contact("81996589021").address("dsfdgsdgsdf").status(false).user(user).build();
        when(customerClient.loadByEmail("carlossoaressantana@hotmail.com")).thenReturn(ResponseEntity.ok(customer));

        Customer sut = authService.loadUserByEmail(user.getEmail());

        assertThat(sut).isEqualTo(customer);
    }
}