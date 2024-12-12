package br.com.loomi.ordermicroservice.models.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDto {

    @NotBlank(message = "Card number is required.")
    @Pattern(
            regexp = "\\d{16}",
            message = "Card number must be exactly 16 digits."
    )
    private String cardNumber;

    @NotNull(message = "Installments are required.")
    @Min(value = 1, message = "Installments must be at least 1.")
    @Max(value = 12, message = "Installments must not exceed 12.")
    private Integer installments;

    @NotBlank(message = "Security code is required.")
    @Pattern(
            regexp = "\\d{3,4}",
            message = "Security code must be 3 or 4 digits."
    )
    private String securityCode;
}
