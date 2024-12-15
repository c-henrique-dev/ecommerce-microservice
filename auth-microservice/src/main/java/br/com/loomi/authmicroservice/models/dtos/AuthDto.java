package br.com.loomi.authmicroservice.models.dtos;

import com.netflix.discovery.provider.Serializer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Serializer
public class AuthDto {
    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email must be valid.")
    private String email;
    
    private String password;
}
