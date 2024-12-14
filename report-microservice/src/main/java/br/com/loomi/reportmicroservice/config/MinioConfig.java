package br.com.loomi.reportmicroservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.bucket}")
    public String bucket;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access.key}")
    private String accessKey;

    @Value("${minio.secret.key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient client = null;

        while (client == null) {
            try {
                client = MinioClient.builder()
                        .endpoint(minioUrl)
                        .credentials(accessKey, secretKey)
                        .build();

                if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                    client.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(bucket)
                                    .build()
                    );
                }

                log.info("Successful connection to MinIO.");
            } catch (Exception e) {
                log.error("Failed to connect to MinIO. Trying again in 3 seconds...");
                TimeUnit.SECONDS.sleep(3); 
            }
        }

        return client;
    }
}
