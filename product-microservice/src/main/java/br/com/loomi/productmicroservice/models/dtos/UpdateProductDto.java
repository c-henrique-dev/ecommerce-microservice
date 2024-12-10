package br.com.loomi.productmicroservice.models.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
@Builder
public class UpdateProductDto {
    @Size(max = 100, message = "Product name must not exceed 100 characters.")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0.")
    @Digits(integer = 10, fraction = 2, message = "Price must be a valid monetary amount with up to 10 digits and 2 decimal places.")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1.")
    private Integer qtd;

    @Size(max = 255, message = "Description must not exceed 255 characters.")
    private String description;
}
