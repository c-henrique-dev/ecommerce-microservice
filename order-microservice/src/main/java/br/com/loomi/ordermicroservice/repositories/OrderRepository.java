package br.com.loomi.ordermicroservice.repositories;

import br.com.loomi.ordermicroservice.models.entities.Order;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @Query("SELECT oi.productId, SUM(oi.qtd) as totalQtd, SUM(oi.qtd * oi.pricePerUnit) as totalValor " +
            "FROM Order o " +
            "JOIN o.orderItems oi " +
            "WHERE o.createdAt BETWEEN :initialDate AND :endDate " +
            "GROUP BY oi.productId")
    List<Object[]> findOrdersByPeriod(@Param("initialDate") LocalDateTime initialDate,
                                      @Param("endDate") LocalDateTime endDate);

    Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
}
