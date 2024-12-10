package br.com.loomi.ordermicroservice.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private UUID productId;
    private String name;
    private Integer qtd;
    private BigDecimal pricePerUnit;

    public BigDecimal getSubtotal() {
        return pricePerUnit.multiply(BigDecimal.valueOf(qtd));
    }
}
