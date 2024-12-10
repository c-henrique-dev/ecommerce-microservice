package br.com.loomi.reportmicroservice.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.ConnectException;

@Service
public class MinioService {
    @Value("${minio.bucket}")
    public String bucket;

    @Value("${minio.put-object-part-size}")
    private Long putObjectPartSize;

    private MinioClient minioClient;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public String uploadReport(MultipartFile file) throws Exception {
        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucket)
                            .object(file.getOriginalFilename())
                            .stream(file.getInputStream(), file.getSize(), putObjectPartSize)
                            .build());
            return file.getOriginalFilename();
        } catch (ConnectException e) {

        }
        return "";
    }
}
