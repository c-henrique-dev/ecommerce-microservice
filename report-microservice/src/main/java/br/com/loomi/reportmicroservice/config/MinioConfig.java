package br.com.loomi.reportmicroservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
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

        // Loop para tentar reconectar até que funcione
        while (client == null) {
            try {
                client = MinioClient.builder()
                        .endpoint(minioUrl)
                        .credentials(accessKey, secretKey)
                        .build();

                // Verificar se o bucket existe, caso contrário criar
                if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                    client.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(bucket)
                                    .build()
                    );
                }

                System.out.println("Conexão bem-sucedida com o MinIO.");
            } catch (Exception e) {
                System.err.println("Falha ao conectar-se ao MinIO. Tentando novamente em 3 segundos...");
                TimeUnit.SECONDS.sleep(3); // Aguardar antes de tentar novamente
            }
        }

        return client;
    }
}
