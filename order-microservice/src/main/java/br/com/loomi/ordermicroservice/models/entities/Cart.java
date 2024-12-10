package br.com.loomi.ordermicroservice.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private UUID customerId;
    private List<CartItem> items = new ArrayList<>();

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem item) {
        this.items.add(item);
    }

    public void removeItem(UUID productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
    }
}
