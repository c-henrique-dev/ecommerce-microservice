package br.com.loomi.ordermicroservice.models.dtos;

import br.com.loomi.ordermicroservice.models.enums.PaymentStatus;
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
public class PaymentDto {
    private String id;
    private UUID orderId;
    private UUID customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private UUID transactionId;
    private String cardNumber;
    private Integer installments;
    private String securityCode;
}