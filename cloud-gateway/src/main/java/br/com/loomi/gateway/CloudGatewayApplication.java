package br.com.loomi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class CloudGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route(r -> r.path("/auth/**").uri("lb://auth-microservice"))
                .route(r -> r.path("/customer/**").uri("lb://customer-microservice"))
                .route(r -> r.path("/product/**").uri("lb://product-microservice"))
                .route(r -> r.path("/order/**").uri("lb://order-microservice"))
                .route(r -> r.path("/report/**").uri("lb://report-microservice"))
                .route(r -> r.path("/payment/**").uri("lb://report-microservice"))
                .build();
    }

}
