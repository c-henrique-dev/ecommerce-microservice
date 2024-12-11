package br.com.loomi.reportmicroservice.services;

import br.com.loomi.reportmicroservice.clients.OrderClient;
import br.com.loomi.reportmicroservice.exceptions.NotFoundException;
import br.com.loomi.reportmicroservice.models.dtos.OrderWithProductDTO;
import br.com.loomi.reportmicroservice.models.dtos.ResponseDto;
import br.com.loomi.reportmicroservice.models.entities.Report;
import br.com.loomi.reportmicroservice.repositories.ReportRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private OrderClient orderClient;

    @Mock
    private MinioService minioService;

    @Mock
    private ReportRepository reportRepository;

    @Test
    public void generateReport_ShouldReturnReportLink_WhenOrdersAreFetchedSuccessfully() throws Exception {
        // Arrange
        LocalDate initialDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        OrderWithProductDTO order1 = new OrderWithProductDTO(UUID.randomUUID(), "Product1", BigDecimal.valueOf(2), 1, new BigDecimal("50.00"));
        OrderWithProductDTO order2 = new OrderWithProductDTO(UUID.randomUUID(), "Product2", BigDecimal.valueOf(3), 1, new BigDecimal("50.00"));
        List<OrderWithProductDTO> orders = Arrays.asList(order1, order2);

        // Mocks
        when(orderClient.getOrdersByPeriod(initialDate, endDate)).thenReturn(ResponseEntity.ok(orders));
        when(minioService.uploadReport(any(MultipartFile.class))).thenReturn("file-path");
        when(reportRepository.save(any(Report.class))).thenReturn(new Report());

        // Act
        ResponseEntity<ResponseDto> response = reportService.generateReport(initialDate, endDate);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getMessage()).contains("http://localhost:9000/report/file-path");

        verify(orderClient).getOrdersByPeriod(initialDate, endDate);
        verify(minioService).uploadReport(any(MultipartFile.class));
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    public void generateReport_ShouldThrowNotFoundException_WhenFeignExceptionOccurs() throws IOException {
        // Arrange
        LocalDate initialDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // Mocks
        when(orderClient.getOrdersByPeriod(initialDate, endDate)).thenThrow(FeignException.class);

        // Act & Assert
        assertThatThrownBy(() -> reportService.generateReport(initialDate, endDate))
                .isInstanceOf(NotFoundException.class);

        verify(orderClient).getOrdersByPeriod(initialDate, endDate);
    }
}
