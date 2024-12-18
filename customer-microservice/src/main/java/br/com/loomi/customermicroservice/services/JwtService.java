package br.com.loomi.customermicroservice.services;

import br.com.loomi.customermicroservice.models.entities.Customer;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.expiration}")
    public String expiration;

    @Value("${security.jwt.key-signature}")
    public String keySignature;

    public String getToken(Customer customer) {
        try {
            long expString = Long.valueOf(expiration);
            LocalDateTime expiration = LocalDateTime.now().plusHours(expString);
            Instant instant = expiration.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            Algorithm algorithm = Algorithm.HMAC512(keySignature);
            String token = JWT.create()
                    .withSubject(customer.getUser().getEmail())
                    .withClaim("id", String.valueOf(customer.getId()))
                    .withExpiresAt(date)
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error generating token");
        }
    }

    public String validToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(keySignature);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

}