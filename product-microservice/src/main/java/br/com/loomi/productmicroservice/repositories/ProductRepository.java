package br.com.loomi.productmicroservice.repositories;

import br.com.loomi.productmicroservice.models.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
