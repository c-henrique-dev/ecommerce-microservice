package br.com.loomi.reportmicroservice.repositories;

import br.com.loomi.reportmicroservice.models.entities.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
}
