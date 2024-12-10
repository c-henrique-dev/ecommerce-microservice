package br.com.loomi.ordermicroservice.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderWithProductDTO {
    private UUID productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer totalQtd;
    private BigDecimal totalValor;
}
