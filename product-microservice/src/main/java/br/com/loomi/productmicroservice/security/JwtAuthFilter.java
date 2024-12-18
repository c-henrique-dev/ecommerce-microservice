package br.com.loomi.productmicroservice.security;

import br.com.loomi.productmicroservice.clients.CustomerClient;
import br.com.loomi.productmicroservice.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer")) {
            String token = authorization.split(" ")[1];
            String isValid = jwtService.validToken(token);

            if (!isValid.isEmpty()) {
                String userLogin = jwtService.validToken(token);
                CustomUserDetails user = customerClient.loadByEmail(userLogin).getBody();
                UsernamePasswordAuthenticationToken userPassword = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                userPassword.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userPassword);
            }
        }
        filterChain.doFilter(request, response);
    }
}