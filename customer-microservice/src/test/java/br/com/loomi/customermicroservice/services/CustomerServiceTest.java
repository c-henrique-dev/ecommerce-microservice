package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.exceptions.NotFoundException;
import br.com.loomi.customermicroservice.models.dtos.CustomerDto;
import br.com.loomi.customermicroservice.models.dtos.UpdateCustomerDto;
import br.com.loomi.customermicroservice.models.dtos.UpdateUserDto;
import br.com.loomi.customermicroservice.models.dtos.UserDto;
import br.com.loomi.customermicroservice.models.entities.Customer;
import br.com.loomi.customermicroservice.models.entities.User;
import br.com.loomi.customermicroservice.models.enums.UserType;
import br.com.loomi.customermicroservice.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;

    @Mock
    private MailService mailService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    public void createCustomer_WithValidData_ReturnsCustomer() {
        User user = User.builder()
                .email("carlossoaressantana@hotmail.com")
                .type(UserType.CUSTOMER)
                .password("123456")
                .build();

        Customer customer = Customer.builder()
                .fullName("Carlos Henrique")
                .contact("81996589021")
                .address("dsfdgsdgsdf")
                .status(false)
                .user(user)
                .build();

        UserDto userDto = UserDto.builder()
                .type(user.getType())
                .password("123456")
                .email(user.getEmail())
                .build();

        CustomerDto customerDto = CustomerDto.builder()
                .fullName(customer.getFullName())
                .contact(customer.getContact())
                .address(customer.getAddress())
                .userDto(userDto)
                .build();

        when(customerRepository.save(Mockito.any(Customer.class))).thenReturn(customer);
        when(passwordEncoder.encode(user.getPassword())).thenReturn(user.getPassword());

        Customer sut = customerService.save(customerDto);

        assertThat(sut).isEqualTo(customer);
        assertThat(sut).isNotNull();
        assertThat(sut.getFullName()).isEqualTo(customer.getFullName());
        assertThat(sut.getContact()).isEqualTo(customer.getContact());

    }

    @Test
    public void createCustomer_WithInvalidData_ThrowsException() {
        User user = User.builder()
                .email("carlossoaressantana@hotmail.com")
                .type(UserType.CUSTOMER)
                .password("123456")
                .build();

        Customer customer = Customer.builder()
                .fullName("Carlos Henrique")
                .contact("81996589021")
                .address("dsfdgsdgsdf")
                .status(false)
                .user(user)
                .build();

        UserDto userDto = UserDto.builder()
                .type(user.getType())
                .password("123456")
                .email(user.getEmail())
                .build();

        CustomerDto customerDto = CustomerDto.builder()
                .fullName(customer.getFullName())
                .contact(customer.getContact())
                .address(customer.getAddress())
                .userDto(userDto)
                .build();

        when(customerRepository.save(customer)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> customerService.save(customerDto)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void removeCustomer_WithExistingId_doesNotThrowAnyException() {
        UUID customerId = UUID.randomUUID();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("carlossoaressantana@hotmail.com")
                .type(UserType.CUSTOMER)
                .password("123456")
                .build();

        Customer customer = Customer.builder()
                .id(customerId)
                .fullName("Carlos Henrique")
                .contact("81996589021")
                .address("dsfdgsdgsdf")
                .status(false)
                .user(user)
                .build();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(customerRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(customer)); // Simula o retorno correto do repositório
        when(customerRepository.findById(any(UUID.class))).thenReturn(Optional.of(customer));

        assertThatCode(() -> customerService.delete(customer.getId())).doesNotThrowAnyException();
    }

    @Test
    public void removeCustomer_WithUnexistingId_ThrowsException() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("carlossoaressantana@hotmail.com")
                .type(UserType.CUSTOMER)
                .password("123456")
                .build();

        Customer customer = Customer.builder()
                .id(UUID.randomUUID())
                .fullName("Carlos Henrique")
                .contact("81996589021")
                .address("dsfdgsdgsdf")
                .status(false)
                .user(user).build();

        UUID invalidId = UUID.randomUUID();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(customerRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(invalidId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void listCustomers_ReturnsAllCustomers() throws Exception {
        Customer customer1 = Customer.builder()
                .id(UUID.randomUUID())
                .fullName("Carlos Henrique")
                .contact("81996589021")
                .build();

        Customer customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .fullName("Henrique Carlos")
                .contact("81996589022")
                .build();

        List<Customer> customersList = List.of(customer1, customer2);
        Pageable pageable = Pageable.ofSize(10);

        when(customerRepository.findAll(any(), eq(pageable)))
                .thenReturn(new PageImpl<>(customersList, pageable, customersList.size()));

        Page<Customer> result = customerService.find(
                Customer.builder().fullName("Carlos Henrique").build(),
                pageable
        );

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactlyInAnyOrder(customer1, customer2);

        verify(customerRepository).findAll(any(), eq(pageable));
    }

    @Test
    public void find_WhenRepositoryReturnsEmptyPage_ShouldReturnEmptyPage() throws Exception {
        Pageable pageable = Pageable.ofSize(10);

        when(customerRepository.findAll(any(), eq(pageable)))
                .thenReturn(new PageImpl<>(Collections.emptyList(), pageable, 0));

        Page<Customer> result = customerService.find(
                Customer.builder().fullName("Carlos Henrique").build(),
                pageable
        );

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(customerRepository).findAll(any(), eq(pageable));
    }

    @Test
    public void update_WhenCustomerExists_ShouldUpdateCustomerSuccessfully() {
        UUID id = UUID.randomUUID();

        User user = User.builder()
                .id(UUID.randomUUID())
                .email("carlossoaressantana@hotmail.com")
                .type(UserType.CUSTOMER)
                .password("123456")
                .build();

        Customer existingCustomer = Customer.builder()
                .id(id)
                .fullName("Carlos")
                .user(User.builder()
                        .email("henrique@hotmail.com")
                        .password("oldPassword")
                        .build())
                .build();

        UpdateCustomerDto updateCustomerDto = UpdateCustomerDto.builder()
                .fullName("Carlos Henrique")
                .userDto(UpdateUserDto.builder()
                        .password("newPassword")
                        .build())
                .build();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(customerRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(existingCustomer));  // Mock do repositório de usuário
        when(customerRepository.findById(id)).thenReturn(Optional.of(existingCustomer));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        customerService.update(id, updateCustomerDto);

        verify(customerRepository).findById(id);
        verify(passwordEncoder).encode("newPassword");
        verify(customerRepository).save(existingCustomer);

        assertThat(existingCustomer.getFullName()).isEqualTo("Carlos Henrique");
        assertThat(existingCustomer.getUser().getPassword()).isEqualTo("encodedPassword");
    }
}
