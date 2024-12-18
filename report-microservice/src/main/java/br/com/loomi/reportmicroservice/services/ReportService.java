package br.com.loomi.reportmicroservice.services;

import br.com.loomi.reportmicroservice.clients.OrderClient;
import br.com.loomi.reportmicroservice.exceptions.NotFoundException;
import br.com.loomi.reportmicroservice.models.dtos.OrderWithProductDTO;
import br.com.loomi.reportmicroservice.models.dtos.ResponseDto;
import br.com.loomi.reportmicroservice.models.entities.Report;
import br.com.loomi.reportmicroservice.repositories.ReportRepository;
import br.com.loomi.reportmicroservice.utils.ByteArrayMultipartFile;
import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ReportService {

    private OrderClient orderClient;
    private MinioService minioService;
    private ReportRepository reportRepository;

    public ReportService(OrderClient orderClient, MinioService minioService, ReportRepository reportRepository) {
        this.orderClient = orderClient;
        this.minioService = minioService;
        this.reportRepository = reportRepository;
    }

    public ResponseEntity<ResponseDto> generateReport(LocalDate initialDate, LocalDate endDate) throws IOException {
        try {
            DateTimeFormatter brazilianDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            String formattedInitialDate = initialDate.format(brazilianDateFormatter);
            String formattedEndDate = endDate.format(brazilianDateFormatter);

            List<OrderWithProductDTO> orders = orderClient.getOrdersByPeriod(initialDate, endDate).getBody();

            String fileName = "report" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(byteArrayOutputStream);

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            writer.write("Product,Qtd,Total,Total Value");
            writer.write("\n");

            for (OrderWithProductDTO order : orders) {
                String productName = order.getProductName();
                int totalQtd = order.getTotalQtd();
                String productPrice = currencyFormatter.format(order.getProductPrice());
                String totalValor = currencyFormatter.format(order.getTotalValor());

                writer.write(String.format("\"%s\",%d,\"%s\",\"%s\"", productName, totalQtd, productPrice, totalValor));
                writer.write("\n");
            }
            writer.flush();

            MultipartFile multipartFile = new ByteArrayMultipartFile(fileName, byteArrayOutputStream.toByteArray());
            String minioFileName = minioService.uploadReport(multipartFile);

            Report report = new Report();
            report.setName("Order Report");
            report.setFilePath(minioFileName);
            report.setCreatedAt(LocalDateTime.now());
            reportRepository.save(report);

            return ResponseEntity.ok(new ResponseDto("http://localhost:9000/report/" + report.getFilePath()));
        } catch (IOException e) {
            throw new RuntimeException("Error generating and sending the report to MinIO");

        } catch (FeignException f) {
            String content = f.contentUTF8();
            String message = content.replaceAll(".*\"message\":\"(.*?)\".*", "$1");
            throw new NotFoundException(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
