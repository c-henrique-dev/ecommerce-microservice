package br.com.loomi.customermicroservice.security;

import br.com.loomi.customermicroservice.clients.AuthClient;
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
    private AuthClient authClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer")) {
            String token = authorization.split(" ")[1];
            String isValid = authClient.validToken(token).getBody();

            if (!isValid.isEmpty()) {
                String userLogin = this.authClient.validToken(token).getBody();
                CustomUserDetails user = this.authClient.loadByUsername(userLogin).getBody();
                UsernamePasswordAuthenticationToken userPassword = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                userPassword.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(userPassword);
            }
        }
        filterChain.doFilter(request, response);
    }


}