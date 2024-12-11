package br.com.loomi.reportmicroservice.controllers;

import br.com.loomi.reportmicroservice.models.dtos.ResponseDto;
import br.com.loomi.reportmicroservice.services.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("report")
@Tag(name = "Report", description = "Endpoints for managing reports")
public class ReportController {

    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("getReport")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(
            summary = "Generate a report",
            description = "Generates a report for the given date range.",
            tags = {"Report"}
    )
    public ResponseEntity<ResponseDto> generateReport(
            @RequestParam("initialDate")
            @Parameter(
                    description = "Start date for the report.",
                    required = true,
                    schema = @Schema(type = "string", format = "date", example = "2024-01-01")
            )
            LocalDate initialDate,

            @RequestParam("endDate")
            @Parameter(
                    description = "End date for the report.",
                    required = true,
                    schema = @Schema(type = "string", format = "date", example = "2024-01-31")
            )
            LocalDate endDate) {
        try {
            return reportService.generateReport(initialDate, endDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
