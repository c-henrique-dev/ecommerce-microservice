package br.com.loomi.authmicroservice.security;

import br.com.loomi.authmicroservice.clients.CustomerClient;
import br.com.loomi.authmicroservice.models.Customer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final CustomerClient customerClient;

    public SecurityService(CustomerClient customerClient) {
        this.customerClient = customerClient;
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

            return this.customerClient.loadByEmail(username).getBody();
        }

        throw new RuntimeException("Usuário não autenticado");
    }
}
