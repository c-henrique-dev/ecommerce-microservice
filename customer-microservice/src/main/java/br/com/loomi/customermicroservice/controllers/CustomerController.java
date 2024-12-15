package br.com.loomi.customermicroservice.controllers;

import br.com.loomi.customermicroservice.models.dtos.CustomerDto;
import br.com.loomi.customermicroservice.models.dtos.UpdateCustomerDto;
import br.com.loomi.customermicroservice.models.entities.Customer;
import br.com.loomi.customermicroservice.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("customer")
@Tag(name = "Customers", description = "Customer management")
public class CustomerController {

    private CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Create a new customer", description = "Creates a new customer using the provided data.")
    public Customer save(@RequestBody @Valid CustomerDto customerDto) {
        return this.customerService.save(customerDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Find customers", description = "Retrieves a paginated list of customers based on the provided filters.")
    public Page<Customer> find(
            @RequestParam(name = "status", required = false) @Parameter(description = "Filter by status") Boolean status,
            @RequestParam(name = "fullName", required = false) @Parameter(description = "Filter by full name") String fullName,
            @ParameterObject Pageable pageable
    ) throws Exception {
        Customer customer = new Customer(status, fullName);
        return this.customerService.find(customer, pageable);
    }

    @GetMapping("get/{email}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get customer by email", description = "Retrieves a customer by their email address.")
    public Customer loadByEmail(@PathVariable @Parameter(description = "Email of the customer") String email) {
        return this.customerService.loadUserByEmail(email);
    }

    @GetMapping("getById/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get customer by id", description = "Retrieves a customer by their id.")
    public Customer findUserEmailByCustomerId(@PathVariable @Parameter(description = "Id of the customer") UUID id) {
        return this.customerService.findByIdWithUser(id);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Update customer", description = "Updates an existing customer with the provided data.")
    public void update(@PathVariable @Parameter(description = "ID of the customer") UUID id,
                       @RequestBody @Valid UpdateCustomerDto updateCustomerDto) {
        this.customerService.update(id, updateCustomerDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @Operation(summary = "Delete customer", description = "Deletes a customer by their ID.")
    public void delete(@PathVariable @Parameter(description = "ID of the customer") UUID id) {
        this.customerService.delete(id);
    }
}
