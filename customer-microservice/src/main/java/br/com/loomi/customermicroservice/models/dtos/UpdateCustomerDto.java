package br.com.loomi.customermicroservice.models.dtos;

import jakarta.validation.Valid;
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
public class UpdateCustomerDto {
    @Valid
    private UpdateUserDto userDto;

    @Size(max = 100, message = "Full name must not exceed 100 characters.")
    private String fullName;

    @Pattern(
            regexp = "\\+?[1-9][0-9]{7,14}",
            message = "Contact must be a valid phone number with up to 15 digits."
    )
    private String contact;

    @Size(max = 255, message = "Address must not exceed 255 characters.")
    private String address;
}
