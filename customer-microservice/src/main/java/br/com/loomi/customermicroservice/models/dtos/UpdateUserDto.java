package br.com.loomi.customermicroservice.models.dtos;

import br.com.loomi.customermicroservice.models.enums.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserDto {
    @Size(max = 50, message = "Name must not exceed 50 characters.")
    private String name;

    @Email(message = "Email must be valid.")
    private String email;

    @Size(min = 8, message = "Password must have at least 8 characters.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
    )
    private String password;

    private UserType type;
}
