package br.com.loomi.reportmicroservice.models.dtos;

import br.com.loomi.reportmicroservice.models.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String password;
    private UserType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
