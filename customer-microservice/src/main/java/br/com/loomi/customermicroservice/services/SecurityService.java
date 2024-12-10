package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.models.entities.Customer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final CustomerService customerService;

    public SecurityService(CustomerService customerService) {
        this.customerService = customerService;
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

            return this.customerService.loadUserByEmail(username);
        }

        throw new RuntimeException("Usuário não autenticado");
    }
}
