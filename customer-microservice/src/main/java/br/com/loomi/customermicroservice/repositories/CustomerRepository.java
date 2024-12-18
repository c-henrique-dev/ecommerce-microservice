package br.com.loomi.customermicroservice.repositories;

import br.com.loomi.customermicroservice.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByUserEmail(String email);

    @Query("SELECT c FROM Customer c LEFT JOIN FETCH c.user WHERE c.id = :id")
    Optional<Customer> findByIdWithUser(UUID id);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Customer c " +
            "WHERE c.user.email = :email")
    boolean existsByUserEmail(String email);
}
