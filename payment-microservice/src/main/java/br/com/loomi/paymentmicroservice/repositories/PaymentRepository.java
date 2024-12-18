package br.com.loomi.paymentmicroservice.repositories;

import br.com.loomi.paymentmicroservice.models.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
