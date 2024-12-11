package br.com.loomi.authmicroservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "auth-microservice", version = "1.0",
        contact = @Contact(name = "Carlos Henrique")))
public class SwaggerConfig {

}