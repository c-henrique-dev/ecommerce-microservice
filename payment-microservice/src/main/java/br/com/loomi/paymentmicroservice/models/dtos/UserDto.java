package br.com.loomi.paymentmicroservice.models;

import br.com.loomi.paymentmicroservice.models.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private UserType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
