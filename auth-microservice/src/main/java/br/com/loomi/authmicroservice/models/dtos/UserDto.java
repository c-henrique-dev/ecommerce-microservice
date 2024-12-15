package br.com.loomi.authmicroservice.models.dtos;

import br.com.loomi.authmicroservice.models.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
    private String name;
    private String email;
    private String password;
    private UserType type;
}
